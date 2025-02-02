// router/index.js
import {createRouter, createWebHistory} from 'vue-router'

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
            path: '/dating-event',
            name: 'DatingEvent',
            component: () => import('@/views/DatingEventPage.vue')
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
                            path: '',
                            redirect: 'list'
                        },
                        {
                            path: 'list',
                            name: 'EmployeeList',
                            component: () => import('@/views/sis/employees/EmployeeListView.vue')
                        },
                        {
                            path: 'attendance',
                            name: 'EmployeeAttendance',
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
                            path: '',
                            redirect: 'history'
                        },
                        {
                            path: 'history',
                            name: 'PaymentHistory',
                            component: () => import('@/views/sis/payments/PaymentHistoryView.vue')
                        },
                        {
                            path: 'stats',
                            name: 'PaymentStats',
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
                        },
                        {
                            path: 'list',
                            name: 'List',
                            component: () => import('@/views/sis/inventory/InventoryListView.vue')
                        },
                        {
                            path: 'history',
                            name: 'History',
                            component: () => import('@/views/sis/inventory/InventoryHistoryView.vue')
                        }
                    ]
                },
                {
                    path: 'customers',
                    component: () => import('@/views/sis/customers/CustomerLayout.vue'),
                    children: [
                        {
                            path: '',
                            redirect: 'list'
                        },
                        {
                            path: 'list',
                            name: 'CustomerList',
                            component: () => import('@/views/sis/customers/CustomerListView.vue')
                        }
                        // {
                        //     path: 'points',
                        //     name: 'CustomerPoints',
                        //     component: () => import('@/views/sis/customers/CustomerPointsView.vue')
                        // }
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
            component: () => import('@/layouts/PosLayout.vue'),
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
        },
        {
            path: '/error/401',
            name: 'Unauthorized',
            component: () => import('@/views/errors/401.vue')
        },
        {
            path: '/error/403',
            name: 'Forbidden',
            component: () => import('@/views/errors/403.vue')
        },
        {
            path: '/error/404',
            name: 'NotFound',
            component: () => import('@/views/errors/404.vue')
        },
        {
            path: '/error/500',
            name: 'ServerError',
            component: () => import('@/views/errors/500.vue')
        },
        // catch all 404
        {
            path: '/:pathMatch(.*)*',
            redirect: '/error/404'
        },
        {
            path: '/meeting',
            component: () => import('@/views/DatingEventPage.vue')
        }
    ]
})

export default router