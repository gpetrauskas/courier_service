import { MenuItem } from '../models/menu-item.model';

export const userMenu: MenuItem[] = [
  {
    title: "Make Order",
    path: `../make-order/`,
    roles: ['USER'],
  },

  {
    title: "My Orders",
    path: '../dashboard/',
    roles: ['USER'],
    children: [
      {
        title: "All orders",
        path: 'user-orders'
      }
    ]
  },

  {
   title: "My Account",
   path: '../dashboard',
   roles: ['USER'],
   children:
   [
    { title: "Saved Addresses", path: 'address' },
    { title: "Payment Methods", path: 'test' }
   ]
  },

  {
    title: "Settings",
    path: '../dashboard/account-settings/',
    roles: ['USER'],
    children:
    [
      { title: "Change Password", path: 'change-password' },
      { title: "Edit Info", path: 'edit-info' }
    ]
  },
  {
    title: 'Manage Tickets',
    path: '../dashboard/ticket/',
    roles: ['USER'],
    children: [
      { title: 'Create Ticket', path: 'create-ticket' },
      { title: "My Tickets", path: 'my-tickets' }
    ]
  }
];
