// router/index.js
import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
    history: createWebHistory(),
    routes: [
        {
            path: '/',
            name: 'Login',
            component: () => import('@/views/LoginView.vue')
        },
        {
            path: '/system',
            name: 'SystemSelect',
            component: () => import('@/views/SystemSelect.vue')
        },
        {
            path: '/sis',
            component: () => import('@/layouts/SisLayout.vue'),
            children: [
                {
                    path: '',
                    redirect: '/sis/dashboard'
                },
                {
                    path: 'dashboard',
                    name: 'SISDashboard',
                    component: () => import('@/views/sis/DashboardView.vue')
                },
                {
                    path: 'sales',
                    name: 'SaleManage',
                    component: () => import('@/views/sis/sales/SalesLayout.vue'),
                    children: [
                        {
                            path: 'status',
                            name: 'SalesStatus',
                            component: () => import('@/views/sis/sales/SalesStatusView.vue')
                        },
                        {
                            path: 'analysis',
                            name: 'SalesAnalysis',
                            component: () => import('@/views/sis/sales/SalesAnalysisView.vue')
                        },
                        {
                            path: 'report',
                            name: 'SalesReport',
                            component: () => import('@/views/sis/sales/SalesReportView.vue')
                        }
                    ]
                },
                {
                    path: 'employees',
                    name: 'Employee',
                    component: () => import('@/views/sis/employees/EmployeeLayout.vue'),
                    children: [
                        {
                            path: 'list',
                            name: 'List',
                            component: () => import('@/views/sis/employees/EmployeeListView.vue')
                        },
                        {
                            path: 'attendance',
                            name: 'Attendance',
                            component: () => import('@/views/sis/employees/AttendanceView.vue')
                        }
                    ]
                },
                {
                    path: 'payments',
                    name: 'Payment',
                    component: () => import('@/views/sis/payments/PaymentLayout.vue'),
                    children: [
                        {
                            path: 'history',
                            name: 'History',
                            component: () => import('@/views/sis/payments/PaymentHistoryView.vue')
                        },
                        {
                            path: 'stats',
                            name: 'Stats',
                            component: () => import('@/views/sis/payments/PaymentStatsView.vue')
                        }
                    ]
                },
                {
                    path: 'inventory',
                    name: 'Inventory',
                    component: () => import('@/views/sis/inventory/InventoryLayout.vue'),
                    children: [
                        {
                            path: 'status',
                            name: 'Status',
                            component: () => import('@/views/sis/inventory/InventoryStatusView.vue')
                        },
                        {
                            path: 'movement',
                            name: 'Movement',
                            component: () => import('@/views/sis/inventory/InventoryMovementView.vue')
                        }
                    ]
                },
                {
                    path: 'system',
                    name: 'SystemManage',
                    component: () => import('@/views/sis/system/SystemManageView.vue')
                },
                {
                    path: 'settings',
                    name: 'Settings',
                    component: () => import('@/views/sis/settings/SettingsView.vue')
                }
            ]
        },
        {
            path: '/pos',
            component: () => import('@/layouts/POSLayout.vue'),
            children: [
                {
                    path: '',
                    name: 'POSMain',
                    component: () => import('@/views/pos/PosMain.vue')
                },
                // {
                //     path: 'products',
                //     name: 'Products',
                //     component: () => import('@/views/pos/ProductsView.vue')
                // },
                // {
                //     path: 'settings',
                //     name: 'Settings',
                //     component: () => import('@/views/pos/SettingsView.vue')
                // }
            ]

        }
    ]
})

export default router