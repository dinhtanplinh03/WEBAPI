const apiBaseUrl = "/api/products";

// Lấy danh sách sản phẩm từ API
async function fetchProducts() {
    try {
        const response = await fetch(apiBaseUrl);
        if (!response.ok) {
            throw new Error("Lỗi khi tải sản phẩm");
        }
        const products = await response.json();
        displayProducts(products);
    } catch (error) {
        console.error("Lỗi:", error);
    }
}

// Hiển thị sản phẩm trong bảng
function displayProducts(products) {
    const productTable = document.getElementById("product-list");
    productTable.innerHTML = "";

    products.forEach(product => {
        const row = document.createElement("tr");
        row.innerHTML = `
            <td>${product.id}</td>
            <td>${product.name}</td>
            <td>${product.price}</td>
            <td>
                <button onclick="editProduct(${product.id})">Sửa</button>
                <button onclick="deleteProduct(${product.id})">Xóa</button>
            </td>
        `;
        productTable.appendChild(row);
    });
}

// Thêm mới sản phẩm
async function addProduct() {
    const name = prompt("Nhập tên sản phẩm:");
    const price = prompt("Nhập giá sản phẩm:");

    if (name && price) {
        const product = { name, price };

        try {
            const response = await fetch(apiBaseUrl, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(product)
            });

            if (!response.ok) throw new Error("Lỗi khi thêm sản phẩm");

            fetchProducts();
        } catch (error) {
            console.error("Lỗi:", error);
        }
    }
}

// Chỉnh sửa sản phẩm
async function editProduct(id) {
    const name = prompt("Nhập tên sản phẩm mới:");
    const price = prompt("Nhập giá mới:");

    if (name && price) {
        const product = { name, price };

        try {
            const response = await fetch(`${apiBaseUrl}/${id}`, {
                method: "PUT",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(product)
            });

            if (!response.ok) throw new Error("Lỗi khi cập nhật sản phẩm");

            fetchProducts();
        } catch (error) {
            console.error("Lỗi:", error);
        }
    }
}

// Xóa sản phẩm
async function deleteProduct(id) {
    if (confirm("Bạn có chắc muốn xóa sản phẩm này không?")) {
        try {
            const response = await fetch(`${apiBaseUrl}/${id}`, {
                method: "DELETE"
            });

            if (!response.ok) throw new Error("Lỗi khi xóa sản phẩm");

            fetchProducts();
        } catch (error) {
            console.error("Lỗi:", error);
        }
    }
}

// Tải sản phẩm khi trang load
document.addEventListener("DOMContentLoaded", fetchProducts);
document.getElementById("addProduct").addEventListener("click", addProduct);
