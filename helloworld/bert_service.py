"""
简单的BERT相似度服务 - 模拟版
"""
from flask import Flask, request, jsonify

app = Flask(__name__)

def mock_similarity(text1, text2):
    words1 = set(text1.replace("，", " ").replace("。", " ").split())
    words2 = set(text2.replace("，", " ").replace("。", " ").split())
    intersection = words1 & words2
    union = words1 | words2
    if not union:
        return 0.0
    jaccard = len(intersection) / len(union)
    if jaccard > 0.3:
        return 0.8 + (jaccard * 0.2)
    return jaccard * 2

@app.route('/best_match', methods=['POST'])
def best_match():
    data = request.get_json()
    current = data.get('current_summary', '')
    candidates = data.get('candidates', [])
    
    best = None
    best_sim = 0
    
    for cand in candidates:
        content = cand.get('content', '')
        if not content:
            continue
        sim = mock_similarity(current, content)
        if sim >= 0.8 and sim > best_sim:
            best_sim = sim
            best = {
                "type": cand.get('type', '未知'),
                "id": cand.get('id', ''),
                "content": content[:100],
                "similarity": round(sim, 4)
            }
    
    return jsonify({"best_match": best})

@app.route('/batch_similarity', methods=['POST'])
def batch_similarity():
    data = request.get_json()
    current = data.get('current_summary', '')
    candidates = data.get('candidates', [])
    
    results = []
    for cand in candidates:
        content = cand.get('content', '')
        if not content:
            continue
        sim = mock_similarity(current, content)
        if sim >= 0.8:
            results.append({
                "type": cand.get('type', '未知'),
                "id": cand.get('id', ''),
                "content": content[:100],
                "similarity": round(sim, 4)
            })
    
    results.sort(key=lambda x: x['similarity'], reverse=True)
    best = results[0] if results else None
    return jsonify({"matched_list": results, "best_match": best})

if __name__ == '__main__':
    print("=" * 60)
    print("BERT相似度服务启动")
    print("  POST /best_match      - 返回最佳匹配")
    print("  POST /batch_similarity - 批量匹配")
    print("=" * 60)
    app.run(host='0.0.0.0', port=5000, debug=True)
