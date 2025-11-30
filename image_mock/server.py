from flask import Flask, jsonify, send_from_directory, request
from flask_cors import CORS
import os
import socket
import json
import random

# 初始化 Flask 应用
app = Flask(__name__)
CORS(app)  # 允许所有跨域请求（开发环境简化配置

file_path="fangdouyin_experience_cards.json"
with open(file_path, 'r', encoding='utf-8') as file:
    data = json.load(file)
# print(data)

MAX_COUNT = 100  # 图片返回最大数量限制（不变）

remaining_indicate = list(range(len(data)))

# 辅助函数：获取服务端局域网 IP（供 Android 访问）
# def get_server_ip():
#     try:
#         s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
#         s.connect(("8.8.8.8", 80))
#         ip = s.getsockname()[0]
#     finally:
#         s.close()
#     return server_ip if (server_ip := os.getenv("SERVER_IP")) else ip

# # 辅助函数：生成图片访问 URL（统一逻辑，避免重复代码）
# def generate_image_url(image_dir, image_name):
#     """
#     生成分类图片的访问 URL
#     :param image_dir: 分类文件夹（如 content_images/avatar_images）
#     :param image_name: 图片文件名
#     :return: 完整 URL（http://服务端IP:5000/static/分类文件夹/文件名）
#     """
#     return f"http://{get_server_ip()}:5000/static/{image_dir}/{image_name}"

def get_itemcards(count):
    if count <= 0:
        count = 10
    elif count > MAX_COUNT:
        count = MAX_COUNT
    selected = []
    selected = random.sample(remaining_indicate, count)
    for i in selected:
        remaining_indicate.remove(i)
    
    datas = []
    print(f"发送{count}条数据:")
    for i in selected:
        print(data[i])
        datas.append(data[i])

    return datas


def is_id_valid(id):
    return ((id>=0)&(id<len(data)))

@app.route("/api/itemcard", methods=["GET"])
def get_all_item():
    # 接口添加 count 参数（客户端请求时传入，如 /api/itemcard?count=5）
    count = request.args.get("count", 10, type=int)
    
    return jsonify({
        "code": 200,
        "data": get_itemcards(count)
    })

@app.route("/api/find", methods=["GET"])
def get_item():
    # 接口添加 id 参数（客户端请求时传入，如 /api/find?id=5）
    id = request.args.get("id", -1,type=int)
    # print(is_id_valid(id))

    if is_id_valid(id):
        print("查询索引id:",id)
        return jsonify({"code": 200,"data": data[id]})
    else:
        return jsonify({"code": 404, "msg": "card_item ID 索引错误"})
        


@app.route("/api/update", methods=["POST"])
def update_likeNum():
    data_back = request.json
    item_id = data_back.get("item_id")  # item_card ID（必传）
    operation = data_back.get("operation")  # 操作类型：add/sub（必传）
    # 1. 参数校验
    if item_id is None or not isinstance(item_id, int):
        return jsonify({"code": 400, "msg": "card_item ID 必须是整数"}), 400
    if operation not in ["add", "sub"]:
        return jsonify({"code": 400, "msg": "操作类型错误，仅支持 add（+1）/ sub（-1）"}), 400
    if not is_id_valid(item_id):
        return jsonify({"code": 404, "msg": f"ID 为 {item_id} 的card_item不存在"}), 404
    
    if operation=="add":
        data[item_id]["LikeNum"]+=1
        print("item_id:",item_id," 点赞计数+1")
        print("现有点赞数:",data[item_id]["LikeNum"])
    else:
        data[item_id]["LikeNum"]-=1
        print("item_id:",item_id," 点赞计数-1")
        print("现有点赞数:",data[item_id]["LikeNum"])
    
    return jsonify({"code": 200}), 200

    




# -------------------------- 错误处理 --------------------------
@app.errorhandler(404)
def not_found(e):
    return jsonify({"code": 404, "msg": "接口或图片不存在"}), 404

@app.errorhandler(500)
def server_error(e):
    return jsonify({"code": 500, "msg": "服务端错误"}), 500

if __name__ == "__main__":
 
    # 启动服务（允许局域网访问）
    app.run(host="0.0.0.0", port=20000, debug=True)
