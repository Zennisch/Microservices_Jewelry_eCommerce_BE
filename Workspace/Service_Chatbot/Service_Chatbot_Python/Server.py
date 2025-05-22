import os
import requests
import logging
from flask import Flask, request, jsonify
from flask_cors import CORS
from google.generativeai import configure, GenerativeModel, types

# Configure logging
logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(name)s - %(levelname)s - %(message)s')
logger = logging.getLogger(__name__)

# Configure Google Gemini API
API_KEY = os.environ.get("GEMINI_API_KEY", "AIzaSyAxSK1D1M1mDrKm9a3_v6L29O4H1G59vf0")
configure(api_key=API_KEY)

# API Gateway base URL
API_GATEWAY = os.environ.get("API_GATEWAY_URL", "http://localhost:8000/api/v1")

# System instruction for the chatbot
system_instruction = """
Bạn là trợ lý ảo của cửa hàng Tinh Tú Jewelry - một thương hiệu trang sức cao cấp.
Nhiệm vụ của bạn là hỗ trợ khách hàng với các thông tin về:
1. Tìm kiếm và giới thiệu sản phẩm trang sức phù hợp với nhu cầu
2. Thông tin về chất liệu, kích thước, giá cả sản phẩm
3. Tư vấn chọn trang sức theo dịp, phong cách, ngân sách
4. Giới thiệu về bộ sưu tập và danh mục sản phẩm
5. Thông tin về xu hướng trang sức và sản phẩm mới, bán chạy

Luôn trả lời lịch sự, chuyên nghiệp và đúng ngữ cảnh về trang sức.
Tuyệt đối: Không được trả lời các câu hỏi không liên quan đến trang sức hoặc cửa hàng.
"""

# Define function declarations for Gemini function calling
get_products_declaration = {
    "name": "get_products",
    "description": "Lấy danh sách sản phẩm trang sức của cửa hàng Tinh Tú",
    "parameters": {
        "type": "object",
        "properties": {
            "limit": {
                "type": "integer",
                "description": "Số lượng sản phẩm muốn hiển thị (mặc định: 5)",
            }
        },
        "required": []
    }
}

get_product_details_declaration = {
    "name": "get_product_details",
    "description": "Lấy thông tin chi tiết về một sản phẩm trang sức cụ thể",
    "parameters": {
        "type": "object",
        "properties": {
            "product_id": {
                "type": "integer",
                "description": "ID của sản phẩm cần xem chi tiết",
            }
        },
        "required": ["product_id"]
    }
}

get_categories_declaration = {
    "name": "get_categories",
    "description": "Lấy danh sách các danh mục trang sức của cửa hàng",
    "parameters": {
        "type": "object",
        "properties": {}
    }
}

get_products_by_category_declaration = {
    "name": "get_products_by_category",
    "description": "Lấy danh sách sản phẩm theo danh mục",
    "parameters": {
        "type": "object",
        "properties": {
            "category_id": {
                "type": "integer",
                "description": "ID của danh mục cần lấy sản phẩm",
            }
        },
        "required": ["category_id"]
    }
}

get_collections_declaration = {
    "name": "get_collections",
    "description": "Lấy danh sách các bộ sưu tập trang sức của cửa hàng",
    "parameters": {
        "type": "object",
        "properties": {}
    }
}

get_products_by_collection_declaration = {
    "name": "get_products_by_collection",
    "description": "Lấy danh sách sản phẩm theo bộ sưu tập",
    "parameters": {
        "type": "object",
        "properties": {
            "collection_id": {
                "type": "integer",
                "description": "ID của bộ sưu tập cần lấy sản phẩm",
            }
        },
        "required": ["collection_id"]
    }
}

get_bestselling_products_declaration = {
    "name": "get_bestselling_products",
    "description": "Lấy danh sách sản phẩm bán chạy nhất",
    "parameters": {
        "type": "object",
        "properties": {}
    }
}

get_new_arrivals_declaration = {
    "name": "get_new_arrivals",
    "description": "Lấy danh sách sản phẩm mới nhất",
    "parameters": {
        "type": "object",
        "properties": {
            "limit": {
                "type": "integer",
                "description": "Số lượng sản phẩm muốn hiển thị (mặc định: 4)",
            }
        },
        "required": []
    }
}

find_products_by_price_range_declaration = {
    "name": "find_products_by_price_range",
    "description": "Tìm sản phẩm trong khoảng giá",
    "parameters": {
        "type": "object",
        "properties": {
            "min_price": {
                "type": "number",
                "description": "Giá thấp nhất (VND)",
            },
            "max_price": {
                "type": "number",
                "description": "Giá cao nhất (VND)",
            }
        },
        "required": ["min_price", "max_price"]
    }
}

find_products_by_material_declaration = {
    "name": "find_products_by_material",
    "description": "Tìm sản phẩm theo chất liệu như vàng, bạc, kim cương, v.v.",
    "parameters": {
        "type": "object",
        "properties": {
            "category_id": {
                "type": "integer",
                "description": "ID của danh mục sản phẩm",
            },
            "material": {
                "type": "string",
                "description": "Chất liệu của sản phẩm (vàng, bạc, kim cương...)",
            }
        },
        "required": ["category_id", "material"]
    }
}

redirect_to_product_declaration = {
    "name": "redirect_to_product",
    "description": "Chuyển hướng người dùng đến trang sản phẩm cụ thể",
    "parameters": {
        "type": "object",
        "properties": {
            "product_id": {
                "type": "integer",
                "description": "ID của sản phẩm cần chuyển hướng đến",
            }
        },
        "required": ["product_id"]
    }
}

# Combine all function declarations
tools = [
    types.Tool(function_declarations=[
        get_products_declaration,
        get_product_details_declaration,
        get_categories_declaration,
        get_products_by_category_declaration,
        get_collections_declaration,
        get_products_by_collection_declaration,
        get_bestselling_products_declaration,
        get_new_arrivals_declaration,
        find_products_by_price_range_declaration,
        find_products_by_material_declaration,
        redirect_to_product_declaration,
    ])
]

# Initialize Gemini model
model = GenerativeModel(
    model_name="gemini-2.0-flash-001",
    tools=tools,
    system_instruction=system_instruction,
)

app = Flask(__name__)
CORS(app)

# Helper function to call the API Gateway
def call_api(endpoint, method="GET", params=None, data=None):
    url = f"{API_GATEWAY}/catalog{endpoint}"
    logger.info(f"Calling API: {method} {url}")
    
    try:
        if method == "GET":
            response = requests.get(url, params=params)
        else:
            response = requests.post(url, json=data)
        
        response.raise_for_status()
        return response.json()
    except requests.exceptions.RequestException as e:
        logger.error(f"API call failed: {e}")
        return None

@app.route("/api/v1/response", methods=["POST"])
def respond():
    try:
        data = request.get_json(force=True)
        prompt = data.get("prompt", "").strip()
        
        logger.info(f"Received prompt: {prompt}")
        
        if not prompt:
            return jsonify({"error": "Missing 'prompt'"}), 400
            
        # Generate response from Gemini
        ai_response = model.generate_content(contents=prompt)
        logger.info(f"Received response from Gemini: {ai_response}")
        
        # Check if we have a function call
        if ai_response.candidates and ai_response.candidates[0].content.parts and \
           hasattr(ai_response.candidates[0].content.parts[0], 'function_call'):
            
            function_call = ai_response.candidates[0].content.parts[0].function_call
            function_name = function_call.name
            function_args = function_call.args
            
            logger.info(f"Function called: {function_name} with args: {function_args}")
            
            # Handle different function calls
            if function_name == "get_products":
                limit = int(function_args.get("limit", 5))
                products = call_api("/products")
                if products:
                    # Limit the number of products returned
                    limited_products = products[:limit]
                    
                    # Use Gemini to generate a nice response about these products
                    product_info = "\n".join([
                        f"- {p.get('name', 'Không tên')} (ID: {p.get('id', 'N/A')}): "
                        f"{p.get('description', 'Không có mô tả')}. "
                        f"Giá: {p.get('price', 'N/A')} VND" 
                        for p in limited_products
                    ])
                    
                    prompt_for_response = f"""
                    Dưới đây là {len(limited_products)} sản phẩm từ cửa hàng Tinh Tú Jewelry:
                    {product_info}
                    
                    Hãy giới thiệu những sản phẩm này cho khách hàng một cách chuyên nghiệp, 
                    nhấn mạnh vào tính độc đáo và chất lượng. Giữ ngôn ngữ tự nhiên và thân thiện,
                    không liệt kê dưới dạng danh sách mà viết thành đoạn văn liền mạch.
                    
                    Vì lí do kỹ thuật, hãy tạm thời trình bày sản phẩm với khách hàng bằng id chứ không phải code. Cảm ơn."
                    """
                    
                    response_text = model.generate_content(prompt_for_response).text
                    return jsonify({"response": response_text}), 200
                else:
                    return jsonify({"response": "Rất tiếc, tôi không thể lấy thông tin sản phẩm lúc này. Vui lòng thử lại sau."}), 200
            
            elif function_name == "get_product_details":
                product_id = function_args.get("product_id")
                product = call_api(f"/products/{product_id}")
                
                if product:
                    # Format product details
                    product_details = f"""
                    Tên sản phẩm: {product.get('name', 'Không có tên')}
                    Mã sản phẩm: {product.get('id', 'Không có mã')}
                    Giá: {product.get('price', 'Không có giá')} VND
                    Mô tả: {product.get('description', 'Không có mô tả')}
                    Chất liệu: {product.get('material', 'Không có thông tin')}
                    Màu sắc: {product.get('color', 'Không có thông tin')}
                    Kích thước: {product.get('size', 'Không có thông tin')}
                    """
                    
                    prompt_for_response = f"""
                    Đây là thông tin chi tiết về sản phẩm mà khách hàng quan tâm:
                    {product_details}
                    
                    Hãy mô tả sản phẩm này một cách hấp dẫn và chuyên nghiệp, đề cập đến các đặc điểm nổi bật 
                    và hướng dẫn khách hàng cách để xem thêm chi tiết hoặc mua sản phẩm.
                    """
                    
                    response_text = model.generate_content(prompt_for_response).text
                    return jsonify({"response": response_text, "product_id": product_id}), 200
                else:
                    return jsonify({"response": "Rất tiếc, tôi không tìm thấy thông tin về sản phẩm này."}), 200
                    
            elif function_name == "get_categories":
                categories = call_api("/categories")
                if categories:
                    category_info = "\n".join([
                        f"- {cat.get('name', 'Không có tên')} (ID: {cat.get('id', 'N/A')}): {cat.get('description', 'Không có mô tả')}"
                        for cat in categories
                    ])
                    
                    prompt_for_response = f"""
                    Dưới đây là các danh mục sản phẩm của Tinh Tú Jewelry:
                    {category_info}
                    
                    Hãy giới thiệu các danh mục này cho khách hàng một cách hấp dẫn, mời họ khám phá các bộ sưu tập.
                    """
                    
                    response_text = model.generate_content(prompt_for_response).text
                    return jsonify({"response": response_text}), 200
                else:
                    return jsonify({"response": "Rất tiếc, tôi không thể lấy thông tin danh mục lúc này."}), 200
                    
            elif function_name == "get_products_by_category":
                category_id = int(function_args.get("category_id"))
                products = call_api(f"/products/category/{category_id}")
                category = call_api(f"/categories/{category_id}")
                
                if products and category:
                    # Format product information
                    product_info = "\n".join([
                        f"- {p.get('name', 'Không tên')} (ID: {p.get('id', 'N/A')}): "
                        f"{p.get('description', 'Không có mô tả')[:100]}... "
                        f"Giá: {p.get('price', 'N/A')} VND" 
                        for p in products[:5]  # Limit to 5 products
                    ])
                    
                    prompt_for_response = f"""
                    Dưới đây là sản phẩm từ danh mục "{category.get('name', 'Không có tên')}" của Tinh Tú Jewelry:
                    {product_info}
                    
                    Hãy giới thiệu ngắn gọn về danh mục này và những sản phẩm tiêu biểu, sử dụng ngôn ngữ chuyên nghiệp
                    và gợi cảm hứng cho khách hàng.
                    """
                    
                    response_text = model.generate_content(prompt_for_response).text
                    return jsonify({"response": response_text}), 200
                else:
                    return jsonify({"response": "Rất tiếc, tôi không thể tìm thấy sản phẩm trong danh mục này."}), 200

            elif function_name == "get_bestselling_products":
                products = call_api("/products/bestselling")
                if products:
                    product_info = "\n".join([
                        f"- {p.get('name', 'Không tên')} (ID: {p.get('id', 'N/A')}): "
                        f"{p.get('description', 'Không có mô tả')[:100]}... "
                        f"Giá: {p.get('price', 'N/A')} VND" 
                        for p in products[:5]
                    ])
                    
                    prompt_for_response = f"""
                    Dưới đây là các sản phẩm bán chạy nhất của Tinh Tú Jewelry:
                    {product_info}
                    
                    Hãy giới thiệu những sản phẩm bán chạy này một cách hấp dẫn, nhấn mạnh vào lý do tại sao 
                    chúng được nhiều khách hàng yêu thích. Sử dụng ngôn ngữ tự nhiên, không liệt kê dạng danh sách.
                    """
                    
                    response_text = model.generate_content(prompt_for_response).text
                    return jsonify({"response": response_text}), 200
                else:
                    return jsonify({"response": "Rất tiếc, tôi không thể lấy thông tin sản phẩm bán chạy lúc này."}), 200
                    
            elif function_name == "get_new_arrivals":
                limit = int(function_args.get("limit", 4))
                products = call_api(f"/products/new-arrivals?limit={limit}")
                
                if products:
                    product_info = "\n".join([
                        f"- {p.get('name', 'Không tên')} (ID: {p.get('id', 'N/A')}): "
                        f"{p.get('description', 'Không có mô tả')[:100]}... "
                        f"Giá: {p.get('price', 'N/A')} VND" 
                        for p in products
                    ])
                    
                    prompt_for_response = f"""
                    Dưới đây là các sản phẩm mới nhất của Tinh Tú Jewelry:
                    {product_info}
                    
                    Hãy giới thiệu những sản phẩm mới này một cách hấp dẫn, nhấn mạnh vào tính mới mẻ, 
                    xu hướng hiện tại và lý do tại sao khách hàng nên quan tâm đến chúng.
                    Sử dụng ngôn ngữ tự nhiên, không liệt kê dạng danh sách.
                    
                    Vì lí do kỹ thuật, hãy tạm thời trình bày sản phẩm với khách hàng bằng id chứ không phải code. Cảm ơn."
                    """
                    
                    response_text = model.generate_content(prompt_for_response).text
                    return jsonify({"response": response_text}), 200
                else:
                    return jsonify({"response": "Rất tiếc, tôi không thể lấy thông tin sản phẩm mới lúc này."}), 200

            elif function_name == "find_products_by_price_range":
                min_price = int(function_args.get("min_price"))
                max_price = int(function_args.get("max_price"))
                
                products = call_api(f"/products/price/between/{min_price}/{max_price}")
                
                if products:
                    product_info = "\n".join([
                        f"- {p.get('name', 'Không tên')} (ID: {p.get('id', 'N/A')}): "
                        f"{p.get('description', 'Không có mô tả')[:100]}... "
                        f"Giá: {p.get('price', 'N/A')} VND" 
                        for p in products[:5]
                    ])
                    
                    prompt_for_response = f"""
                    Dưới đây là các sản phẩm của Tinh Tú Jewelry trong khoảng giá từ {min_price} đến {max_price} VND:
                    {product_info}
                    
                    Hãy giới thiệu những sản phẩm này, nhấn mạnh vào giá trị và chất lượng mà khách hàng nhận được.
                    Giữ ngôn ngữ tự nhiên và thân thiện, không liệt kê dưới dạng danh sách.
                    """
                    
                    response_text = model.generate_content(prompt_for_response).text
                    return jsonify({"response": response_text}), 200
                else:
                    return jsonify({"response": f"Rất tiếc, tôi không tìm thấy sản phẩm nào trong khoảng giá từ {min_price} đến {max_price} VND."}), 200

            elif function_name == "redirect_to_product":
                product_id = int(function_args.get("product_id"))
                
                # Verify product exists
                product = call_api(f"/products/{product_id}")
                if product:
                    redirect_url = f"/catalog/product/{product_id}"
                    return jsonify({
                        "response": f"Tôi đã tìm thấy sản phẩm {product.get('name', '')} mà bạn quan tâm. Tôi sẽ chuyển bạn đến trang sản phẩm ngay bây giờ.",
                        "redirect": redirect_url
                    }), 200
                else:
                    return jsonify({"response": "Rất tiếc, tôi không tìm thấy sản phẩm này."}), 200
            
            else:
                # Handle any other function calls that might be added in the future
                # return jsonify({"response": "Xin lỗi, chức năng này hiện không khả dụng."}), 200
                if ai_response.candidates[0].content.parts[0].text:
                    return jsonify({"response": ai_response.candidates[0].content.parts[0].text}), 200
                else:
                    return jsonify({"response": "Xin lỗi, tôi không hiểu câu hỏi của bạn. Vui lòng thử lại."}), 200
                
        else:
            # If no function was called, return the direct text response
            if ai_response.candidates[0].content.parts[0].text:
                return jsonify({"response": ai_response.candidates[0].content.parts[0].text}), 200
            else:
                return jsonify({"response": "Xin lỗi, tôi không hiểu câu hỏi của bạn. Vui lòng thử lại."}), 200
                
    except Exception as e:
        logger.error(f"Error processing request: {e}", exc_info=True)
        return jsonify({"error": "Đã xảy ra lỗi khi xử lý yêu cầu của bạn"}), 500

if __name__ == "__main__":
    # Get port from environment variable or use default
    app.run(host="0.0.0.0", port=5000, debug=True)
