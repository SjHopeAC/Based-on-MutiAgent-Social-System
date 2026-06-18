import matplotlib.pyplot as plt
import numpy as np

# 设置中文字体，避免乱码
plt.rcParams['font.sans-serif'] = ['SimHei', 'Microsoft YaHei', 'PingFang SC']
plt.rcParams['axes.unicode_minus'] = False

# 数据
metrics = ['准确率', '精确率', '召回率', 'F1 分数']
bert_values = [86.5, 79.2, 81.3, 80.2]
multiagent_values = [91.2, 86.4, 86.9, 86.6]
improvements = [4.7, 7.2, 5.6, 6.4]

x = np.arange(len(metrics))
width = 0.35  # 条形宽度

fig, ax = plt.subplots(figsize=(10, 6))
bars1 = ax.bar(x - width/2, bert_values, width, label='单一 BERT 模型', color='#4C72B0', edgecolor='black')
bars2 = ax.bar(x + width/2, multiagent_values, width, label='多智能体架构', color='#DD8452', edgecolor='black')

# 在条形上方显示数值
for bar in bars1:
    height = bar.get_height()
    ax.annotate(f'{height}%', xy=(bar.get_x() + bar.get_width()/2, height),
                xytext=(0, 3), textcoords="offset points", ha='center', va='bottom', fontsize=10)
for bar in bars2:
    height = bar.get_height()
    ax.annotate(f'{height}%', xy=(bar.get_x() + bar.get_width()/2, height),
                xytext=(0, 3), textcoords="offset points", ha='center', va='bottom', fontsize=10)

# 添加提升幅度标注（在图表顶部）
for i, (imp, bert, multi) in enumerate(zip(improvements, bert_values, multiagent_values)):
    y_max = max(bert, multi) + 5
    ax.annotate(f'+{imp}%', xy=(i, y_max), ha='center', va='bottom',
                fontsize=10, fontweight='bold', color='green')

ax.set_ylabel('百分比 (%)', fontsize=12)
ax.set_title('单一 BERT 模型 vs 多智能体架构 评估效果对比', fontsize=14)
ax.set_xticks(x)
ax.set_xticklabels(metrics)
ax.legend(loc='upper left', fontsize=10)
ax.set_ylim(0, 105)
ax.grid(axis='y', linestyle='--', alpha=0.7)

plt.tight_layout()
plt.show()