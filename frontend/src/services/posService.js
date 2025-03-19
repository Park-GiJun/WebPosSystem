// services/posService.js
import axios from '@/plugins/axios';

class PosService {
    async getProducts(params = {}) {
        try {
            const response = await axios.get('/products', { params });
            return response.data.data;
        } catch (error) {
            console.error('Failed to fetch products:', error);
            throw error;
        }
    }

    async getProductsByCategory(categoryId, params = {}) {
        try {
            const queryParams = {
                ...params,
                categoryId
            };
            const response = await axios.get('/products', { params: queryParams });
            return response.data.data;
        } catch (error) {
            console.error(`Failed to fetch products for category ${categoryId}:`, error);
            throw error;
        }
    }

    async getCategories() {
        try {
            const response = await axios.get('/categories');
            return response.data.data;
        } catch (error) {
            console.error('Failed to fetch categories:', error);
            throw error;
        }
    }

    async createOrder(orderData) {
        try {
            const response = await axios.post('/orders', { data: orderData });
            return response.data.data;
        } catch (error) {
            console.error('Failed to create order:', error);
            throw error;
        }
    }

    async processPayment(paymentData) {
        try {
            const response = await axios.post('/payments', { data: paymentData });
            return response.data.data;
        } catch (error) {
            console.error('Failed to process payment:', error);
            throw error;
        }
    }

    async getSalesData(params = {}) {
        try {
            const response = await axios.get('/sales', { params });
            return response.data.data;
        } catch (error) {
            console.error('Failed to fetch sales data:', error);
            throw error;
        }
    }

    async printReceipt(orderId) {
        try {
            const response = await axios.post(`/print/receipt/${orderId}`);
            return response.data.data;
        } catch (error) {
            console.error('Failed to print receipt:', error);
            throw error;
        }
    }

    // 정산 관련 API
    async getSettlementData(params = {}) {
        try {
            const response = await axios.get('/settlements', { params });
            return response.data.data;
        } catch (error) {
            console.error('Failed to fetch settlement data:', error);
            throw error;
        }
    }

    async closeSettlement(settlementData) {
        try {
            const response = await axios.post('/settlements/close', { data: settlementData });
            return response.data.data;
        } catch (error) {
            console.error('Failed to close settlement:', error);
            throw error;
        }
    }
}

export const posService = new PosService();