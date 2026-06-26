import { MenuItem } from '../models/menu-item.model';

export const adminMenu: MenuItem[] = [
  {
    title: 'Manage User',
    path: '../admin-dashboard/manage-user',
    roles: ['ADMIN'],
      children: [
        { path: 'all-users', title: 'All Users' },
        { path: 'find-user', title: 'Find User' }
      ]
  },
  {
    title: 'Manage Orders',
    path: '../admin-dashboard/manage-order',
    roles: ['ADMIN'],
      children : [
          { path: 'all-orders', title: 'All Orders' }
      ]
  },
  {
    title: 'Manage Delivery Lists',
    path: '../admin-dashboard/delivery-control',
    roles: ['ADMIN'],
      children: [
        { path: 'all-lists', title: 'All Delivery Lists' },
        { path: 'create-new-list', title: 'Create New List' }
      ]
  },
  {
    title: 'Manage Pricing Options',
    path: '../admin-dashboard/manage-delivery-option',
    roles: ['ADMIN'],
      children: [
        { path: 'add-delivery-option', title: 'Add Option' },
        { path: 'all-delivery-options', title: 'All Options' }
      ]
  },
  {
    title: 'Manage Couriers',
    path: '../admin-dashboard/manage-courier',
    roles: ['ADMIN'],
      children: [
        { path: 'register-courier', title: 'Register New Courier' }
      ]
  },
  {
    title: "Manage Notifications",
    path: 'admin-dashboard/notification',
    roles: ['ADMIN'],
    children: [
      { path: 'create', title: 'Create New' },
      { path: 'manage', title: 'Manage' }
    ]
  },
  {
    title: "Tickets",
    path: 'admin-dashboard/tickets',
    roles: ['ADMIN'],
    children: [
      { title: "All", path: 'all' },
    ]
  }


];
