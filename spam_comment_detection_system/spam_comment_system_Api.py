# bert_sentiment_flask.py
"""
BERT 情感分析 API 服务 - Flask 版本
使用 GET 方法接收文本参数，返回情感分析结果
若 BERT 置信度 > 0.8 则直接返回，否则转交多智能体辩论处理
"""

import os
import json
import asyncio
import torch
from transformers import BertTokenizer, BertForSequenceClassification
from flask import Flask, request, jsonify
from flask_cors import CORS
from dotenv import load_dotenv
import requests

# 多智能体相关
from autogen_ext.models.openai import OpenAIChatCompletionClient
from autogen_agentchat.agents import AssistantAgent
from autogen_agentchat.teams import RoundRobinGroupChat
from autogen_agentchat.conditions import MaxMessageTermination
from autogen_core.tools import FunctionTool

load_dotenv()

# ============ 1. BERT 模型加载器 ============
LABEL_NAMES = {0: '负面', 1: '中性', 2: '正面'}
INVERSE_LABEL_MAPPING = {0: -1, 1: 0, 2: 1}


class BertModelLoader:
    """BERT模型加载器：用于情感分析"""

    def __init__(self, model_path="D:\\Python\\pythonProject2\\Python4\\Agent\\test\\bert_spam_model"):
        self.model_path = model_path
        self.device = "cuda" if torch.cuda.is_available() else "cpu"
        self.tokenizer = None
        self.model = None
        self.max_len = 128
        self._load_model()

    def _load_model(self):
        """加载BERT模型和分词器"""
        try:
            print(f"正在加载BERT模型: {self.model_path}")
            print(f"使用设备: {self.device}")

            self.tokenizer = BertTokenizer.from_pretrained(self.model_path)
            self.model = BertForSequenceClassification.from_pretrained(self.model_path).to(self.device)
            self.model.eval()

            print("BERT情感分析模型加载完成！")
        except Exception as e:
            print(f"BERT模型加载失败: {e}")
            self.model = None
            self.tokenizer = None

    def predict(self, text: str) -> dict:
        """使用BERT模型进行情感分析"""
        if self.model is None or self.tokenizer is None:
            return {
                "spam_probability": 0.5,
                "reasoning": "BERT模型未加载",
                "sentiment": "中性",
                "max_confidence": 0.0
            }

        try:
            inputs = self.tokenizer(
                text,
                return_tensors="pt",
                max_length=self.max_len,
                truncation=True,
                padding='max_length'
            ).to(self.device)

            with torch.no_grad():
                outputs = self.model(**inputs)
                probabilities = torch.softmax(outputs.logits, dim=-1)[0]
                pred_label = probabilities.argmax().item()

            negative_prob = probabilities[0].item()
            neutral_prob = probabilities[1].item()
            positive_prob = probabilities[2].item()

            max_confidence = max(negative_prob, neutral_prob, positive_prob)
            spam_probability = negative_prob

            if pred_label == 0:
                sentiment = "负面"
                reasoning = f"BERT情感分析：负面情感 (负面={negative_prob:.3f}, 中性={neutral_prob:.3f}, 正面={positive_prob:.3f})"
            elif pred_label == 1:
                sentiment = "中性"
                reasoning = f"BERT情感分析：中性情感 (负面={negative_prob:.3f}, 中性={neutral_prob:.3f}, 正面={positive_prob:.3f})"
            else:
                sentiment = "正面"
                reasoning = f"BERT情感分析：正面情感 (负面={negative_prob:.3f}, 中性={neutral_prob:.3f}, 正面={positive_prob:.3f})"

            return {
                "spam_probability": spam_probability,
                "reasoning": reasoning,
                "sentiment": sentiment,
                "negative_prob": negative_prob,
                "neutral_prob": neutral_prob,
                "positive_prob": positive_prob,
                "max_confidence": max_confidence
            }
        except Exception as e:
            print(f"BERT预测失败: {e}")
            return {
                "spam_probability": 0.5,
                "reasoning": f"BERT预测失败: {str(e)}",
                "sentiment": "中性",
                "max_confidence": 0.0
            }


# 全局BERT模型实例
bert_model_loader = None


def get_bert_model():
    """获取BERT模型实例"""
    global bert_model_loader
    if bert_model_loader is None:
        bert_model_loader = BertModelLoader()
    return bert_model_loader


# ============ 2. 多智能体辩论模块 ============

class BertSpamDetector:
    """BERT垃圾评论检测器（多智能体专用）"""

    def __init__(self, model_path="D:\\Python\\pythonProject2\\Python4\\Agent\\test\\bert_spam_model"):
        self.model_path = model_path
        self.device = "cuda" if torch.cuda.is_available() else "cpu"
        self.tokenizer = None
        self.model = None
        self.max_len = 128
        self._load_model()

    def _load_model(self):
        try:
            print(f"[MultiAgent] 加载BERT模型: {self.model_path}")
            self.tokenizer = BertTokenizer.from_pretrained(self.model_path)
            self.model = BertForSequenceClassification.from_pretrained(self.model_path).to(self.device)
            self.model.eval()
            print("✅ [MultiAgent] BERT模型加载完成")
        except Exception as e:
            print(f"❌ [MultiAgent] BERT加载失败: {e}")
            self.model = None

    def analyze(self, text: str) -> dict:
        if self.model is None:
            return {"observation": "BERT模型未加载"}

        try:
            inputs = self.tokenizer(
                text,
                return_tensors="pt",
                max_length=self.max_len,
                truncation=True,
                padding='max_length'
            ).to(self.device)

            with torch.no_grad():
                outputs = self.model(**inputs)
                probabilities = torch.softmax(outputs.logits, dim=-1)[0]
                pred_label = probabilities.argmax().item()

            neg_prob = probabilities[0].item()
            neu_prob = probabilities[1].item()
            pos_prob = probabilities[2].item()

            observations = []
            if pred_label == 0:
                observations.append(f"情感分析显示负面倾向（负面{neg_prob:.1%}）")
            elif pred_label == 1:
                observations.append(f"情感分析显示中性（中性{neu_prob:.1%}）")
            else:
                observations.append(f"情感分析显示正面倾向（正面{pos_prob:.1%}）")

            keywords = []
            if "扫码" in text:
                keywords.append("扫码")
            if "加V" in text or "微信" in text:
                keywords.append("微信号")
            if "免费" in text:
                keywords.append("免费")
            if "代购" in text:
                keywords.append("代购")
            if "广告" in text:
                keywords.append("广告")

            if keywords:
                observations.append(f"检测到关键词：{', '.join(keywords)}")

            return {"observation": "；".join(observations)}
        except Exception as e:
            return {"observation": f"分析出错: {str(e)}"}


_bert_detector = None


def get_bert_detector():
    global _bert_detector
    if _bert_detector is None:
        _bert_detector = BertSpamDetector()
    return _bert_detector


class RealAPIService:
    """真实API服务，通过HTTP请求获取环境上下文和用户行为"""

    @staticmethod
    def get_environment_context(post_id: str) -> dict:
        """根据post_id获取帖子环境上下文"""
        try:
            url = f"http://localhost:8088/api/spam/post/{post_id}"
            response = requests.get(url, timeout=5)
            response.raise_for_status()
            data = response.json()
            # 根据实际返回结构调整，此处假设API直接返回包含observation字段的字典
            return data
        except Exception as e:
            print(f"获取环境上下文失败: {e}")
            # 降级返回默认数据
            return {"observation": f"环境上下文获取失败，post_id={post_id}"}

    @staticmethod
    def get_user_behavior(user_id: str) -> dict:
        """根据user_id获取用户行为画像"""
        try:
            url = f"http://localhost:8088/api/spam/user/{user_id}"
            response = requests.get(url, timeout=5)
            response.raise_for_status()
            data = response.json()
            return data
        except Exception as e:
            print(f"获取用户行为失败: {e}")
            return {"observation": f"用户行为获取失败，user_id={user_id}"}


def analyze_with_bert(text: str) -> str:
    print(f"🔧 调用BERT分析: {text[:30]}...")
    detector = get_bert_detector()
    result = detector.analyze(text)
    return json.dumps(result, ensure_ascii=False)


def get_environment_context(post_id: str = "1") -> str:
    """环境上下文工具（现在通过post_id请求真实API）"""
    print(f"🔧 调用环境分析: post_id={post_id}")
    result = RealAPIService.get_environment_context(post_id)
    return json.dumps(result, ensure_ascii=False)


def analyze_user_behavior(user_id: str) -> str:
    """用户行为分析工具（现在通过user_id请求真实API）"""
    print(f"🔧 调用用户分析: user_id={user_id}")
    result = RealAPIService.get_user_behavior(user_id)
    return json.dumps(result, ensure_ascii=False)


bert_tool = FunctionTool(
    name="analyze_with_bert",
    description="使用BERT模型分析评论文本，返回情感和关键词观察",
    func=analyze_with_bert,
)

context_tool = FunctionTool(
    name="get_environment_context",
    description="根据帖子ID获取评论发布的环境上下文信息（通过HTTP API）",
    func=get_environment_context,
)

user_tool = FunctionTool(
    name="analyze_user_behavior",
    description="根据用户ID获取评论发布的用户画像信息（通过HTTP API）",
    func=analyze_user_behavior,
)

all_tools = [bert_tool, context_tool, user_tool]


def create_bert_analyst_agent(model_client):
    system_message = """你是BERT分析师，也是辩论参与者。

你的职责：
1. 第一轮：调用 analyze_with_bert 获取文本分析
2. 后续轮次：阅读其他人的观点，提出质疑或补充
3. 请思考的尽量简短并尽快输出合理的答案

辩论技巧：
- 如果别人质疑你，要解释你的观察
- 指出别人观点中的漏洞
- 提供新的观察角度

记住：这是多轮辩论，不是一次定论！"""
    return AssistantAgent(
        name="bert_analyst",
        model_client=model_client,
        tools=all_tools,
        system_message=system_message,
        reflect_on_tool_use=True,
    )


def create_context_analyst_agent(model_client):
    system_message = """你是环境分析师，也是辩论参与者。

你的职责：
1. 第一轮：调用 get_environment_context 获取环境分析
2. 后续轮次：阅读其他人的观点，提出质疑或补充
3. 请思考的尽量简短并尽快输出合理的答案

辩论技巧：
- 从环境角度解释为什么某些关键词是正常的
- 指出时间因素对评论的影响
- 反驳过度解读的观点

记住：这是多轮辩论，不是一次定论！"""
    return AssistantAgent(
        name="context_analyst",
        model_client=model_client,
        tools=all_tools,
        system_message=system_message,
        reflect_on_tool_use=True,
    )


def create_user_analyst_agent(model_client):
    system_message = """你是用户分析师，也是辩论参与者。

你的职责：
1. 第一轮：调用 analyze_user_behavior 获取用户分析
2. 后续轮次：阅读其他人的观点，提出质疑或补充
3. 请思考的尽量简短并尽快输出合理的答案

辩论技巧：
- 用用户历史行为解释当前评论
- 指出新用户和老用户的区别
- 反驳不考虑用户背景的判断

记住：这是多轮辩论，不是一次定论！"""
    return AssistantAgent(
        name="user_analyst",
        model_client=model_client,
        tools=all_tools,
        system_message=system_message,
        reflect_on_tool_use=True,
    )


def create_judge_agent(model_client):
    system_message = """你是首席裁决官，负责总结整个辩论并输出严格的JSON格式判决。

    ### 强制要求（必须严格遵守，否则判你失职）：
    1. **在前2轮中，你要客观总结观察和记录，尽量充分发挥各自智能体的自主性，不要把讨论变为答疑**。
    2. 直到第3轮（即三个分析师共发言9次后），你才能给出最终JSON裁决。

    在输出层面：
    1. 只输出JSON字符串，**不要任何多余文字、解释、换行、符号**
    2. JSON必须包含且仅包含以下3个字段：
       - confidence: 0-1之间的浮点数（如0.85）
       - result: 只能是"是"、"否"、"未知"三者之一
       - reasoning: 字符串，引用辩论关键论点
    3. 禁止输出JSON以外的任何内容（包括"辩论结束"、"最终判决："等）
    4. JSON中**禁止使用单引号**，字符串必须用双引号

    ### 输出示例（必须完全模仿这个格式）：
    {"confidence":0.9,"result":"是","reasoning":"BERT检测到广告关键词，用户行为显示该账号多次发布营销内容，环境上下文显示发布在科技板块（非广告允许区域），综合判断为垃圾评论"}

    ### 判断原则：
    - 置信度≥0.7 → result为"是"或"否"
    - 置信度<0.7 → result为"未知"
    - reasoning必须引用辩论中的具体观点，不能空泛
    - 记住：让辩论充分进行，不要在第一轮就下结论！"""
    return AssistantAgent(
        name="chief_judge",
        model_client=model_client,
        tools=all_tools,
        system_message=system_message,
        reflect_on_tool_use=True,
    )


def create_openai_model_client():
    os.environ["OPENAI_API_KEY"] = os.getenv("LLM_API_KEY", "")
    os.environ["OPENAI_BASE_URL"] = os.getenv("LLM_BASE_URL", "")

    model = os.getenv("LLM_MODEL_ID", "coding-glm-4.7-free")
    api_key = os.getenv("OPENAI_API_KEY")
    base_url = os.getenv("OPENAI_BASE_URL")

    if not all([api_key, base_url]):
        raise ValueError("请在.env文件中配置API信息")

    model_info = {
        "model_name": model,
        "context_window": 8192,
        "vision": False,
        "function_calling": True,
        "structured_output": True,
        "json_output": True,
        'family': "glm",
        "max_tokens": 4096,
        "temperature": 0.7
    }

    return OpenAIChatCompletionClient(
        model=model,
        api_key=api_key,
        base_url=base_url,
        model_info=model_info,
        timeout=180,
        max_retries=5,
        retry_wait_min=1,  # 初始等待1秒
        retry_wait_max=60,  # 最大等待60秒
        retry_wait_multiplier=2,  # 指数退避 (1,2,4,8,16)
    )


def extract_json_from_output(text: str) -> dict:
    """从文本中提取JSON数据（超强容错版）"""
    try:
        start_idx = text.find("{")
        end_idx = text.rfind("}") + 1
        if start_idx == -1 or end_idx == 0:
            return {"error": "未找到JSON格式内容", "raw_text": text}

        json_str = text[start_idx:end_idx].strip()
        json_str = json_str.replace("“", '"').replace("”", '"').replace("‘", "'").replace("’", "'")

        import re
        def replace_inner_quotes(match):
            content = match.group(1)
            content = content.replace("'", "\\'")
            return f'"{content}"'

        pattern = r'"([^"\\]*(\\.[^"\\]*)*)"'
        json_str = re.sub(pattern, replace_inner_quotes, json_str)
        json_str = re.sub(r"'([^']+)'(\s*:)", r'"\1"\2', json_str)

        try:
            json_data = json.loads(json_str)
        except json.JSONDecodeError:
            escaped_str = ""
            in_quotes = False
            for char in json_str:
                if char == '"' and not escaped_str.endswith('\\'):
                    in_quotes = not in_quotes
                    escaped_str += char
                elif char == "'" and in_quotes:
                    escaped_str += "\\'"
                else:
                    escaped_str += char
            json_data = json.loads(escaped_str)

        required_fields = ["confidence", "result", "reasoning"]
        for field in required_fields:
            if field not in json_data:
                return {"error": f"缺少必填字段：{field}", "raw_json": json_data}

        if json_data["result"] not in ["是", "否", "未知"]:
            return {"error": "result值非法，必须是是/否/未知", "raw_json": json_data}

        if not (0 <= json_data["confidence"] <= 1):
            return {"error": "confidence必须在0-1之间", "raw_json": json_data}

        return {"success": True, "data": json_data}
    except Exception as e:
        return {"error": f"提取JSON出错：{str(e)}", "raw_text": text}


async def run_multiagent_debate(user_id: str,post_id:str,comment: str) -> dict:
    """
    运行多智能体辩论，返回最终判决结果
    返回格式：{"result": "是/否/未知", "confidence": float, "reasoning": str}
    """
    print(f"\nSystem:启动多智能体辩论 (用户: {user_id},帖子: {post_id},评论: {comment})")

    # 初始化BERT检测器（如果尚未加载）
    get_bert_detector()

    model_client = create_openai_model_client()

    bert = create_bert_analyst_agent(model_client)
    context = create_context_analyst_agent(model_client)
    user = create_user_analyst_agent(model_client)
    judge = create_judge_agent(model_client)

    max_msg_termination = MaxMessageTermination(max_messages=16)
    team = RoundRobinGroupChat(
        participants=[bert, context, user, judge],
        termination_condition=max_msg_termination,
        max_turns=12,
    )

    task = f"""System:现在开始多轮辩论，判断这条评论是否为垃圾评论：

用户ID: {user_id}
帖子ID: {post_id}
评论: {comment}

辩论规则：
【第1轮】每个人先调用工具获取自己的观察
【第2轮】由每个智能体互相质疑和补充
【第3轮】深入辩论，指出矛盾，裁决官发言总结，输出JSON

强制规则：
- 辩论专家们：要互相质疑，不要轻易同意对方
- 裁决官：等辩论充分后再下结论，至少要等看到第3轮的观点
- 系统会保证进行至少3轮辩论
- 裁决官最终必须只输出JSON格式，不要任何多余文字

现在开始第一轮辩论！"""

    from autogen_agentchat.messages import TextMessage
    messages_collect = []

    print("System:辩论进行中...")
    async for msg in team.run_stream(task=task):
        if isinstance(msg, TextMessage):
            print(f"[{msg.source}] {msg.content[:200]}...")  # 只打印前100字符，避免刷屏
            messages_collect.append({
                "name": msg.source,
                "content": msg.content
            })

    # 提取裁决官最后一条消息
    judge_messages = [m for m in messages_collect if m["name"] == "chief_judge"]
    if judge_messages:
        final_judgment = judge_messages[-1]["content"]
        print(f"🔍 裁决官原始输出：{final_judgment[:200]}...")
        json_result = extract_json_from_output(final_judgment)
        if json_result.get("success"):
            return json_result["data"]
        else:
            # 降级返回
            return {
                "result": "未知",
                "confidence": 0.5,
                "reasoning": f"JSON提取失败: {json_result.get('error')}"
            }
    else:
        return {
            "result": "未知",
            "confidence": 0.5,
            "reasoning": "未找到裁决官消息"
        }


# ============ 3. Flask 应用 ============
app = Flask(__name__)
CORS(app)


@app.route('/health', methods=['GET'])
def health_check():
    return jsonify({"status": "ok", "message": "BERT情感分析服务运行中"})


@app.route('/predict', methods=['GET'])
def predict_sentiment():
    """
    情感分析预测接口 - GET 方法

    参数：
        text: 需要分析的评论文本（必填）

    逻辑：
        - BERT 最高置信度 > 0.85 → 直接返回 BERT 结果
        - 否则 → 调用多智能体辩论返回最终判决
    """
    text = request.args.get('text', '')
    user_id = request.args.get('user_id', '0')  # 可选参数
    post_id = request.args.get('post_id', '1')  # 可选参数

    if not text or not text.strip():
        return jsonify({
            "error": "参数错误",
            "message": "text 参数不能为空"
        }), 400

    text = text.strip()

    # 1. BERT 预测
    bert = get_bert_model()
    bert_result = bert.predict(text)

    # 2. 判断置信度
    CONFIDENCE_THRESHOLD = 0.85
    max_conf = bert_result.get("max_confidence", 0.0)
    if bert_result["sentiment"] == "负面":
        result="是"
    else:
        result="否"

    print(f"BERT 最高置信度: {max_conf:.3f}")

    if max_conf >= CONFIDENCE_THRESHOLD:
        # 高置信度，直接返回 BERT 结果
        print("✅ 高置信度，直接返回 BERT 结果")
        return jsonify({
            "confidence": max_conf,
            "reasoning": bert_result["reasoning"],
            "result": result,
            "source": "bert"
        })
    else:
        # 低置信度，转交多智能体辩论
        print("⚠️ 置信度不足，启动多智能体辩论...")
        try:
            # 在同步 Flask 中调用异步函数
            debate_result = asyncio.run(run_multiagent_debate(user_id,post_id,text))
            return jsonify({
                "source": "multiagent",
                "result": debate_result.get("result", "未知"),
                "confidence": debate_result.get("confidence", 0.5),
                "reasoning": debate_result.get("reasoning", ""),
                "bert_preliminary": {
                    "sentiment": bert_result["sentiment"],
                    "max_confidence": max_conf
                }
            })
        except Exception as e:
            print(f"多智能体辩论出错: {e}")
            # 出错时降级返回 BERT 结果
            return jsonify({
                "source": "bert_fallback",
                "error": f"多智能体辩论失败: {str(e)}",
                "sentiment": bert_result["sentiment"],
                "spam_probability": bert_result["spam_probability"],
                "reasoning": bert_result["reasoning"]
            })


# ============ 4. 启动服务 ============
if __name__ == "__main__":
    print("正在初始化 BERT 模型...")
    get_bert_model()
    print("服务启动中...")

    app.run(
        host='0.0.0.0',
        port=8000,
        debug=False,
        threaded=True
    )