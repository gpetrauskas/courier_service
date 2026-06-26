import { MenuItem } from '../models/menu-item.model';

export const courierMenu: MenuItem[] = [
  {
    title: 'Tasks',
    path: '../courier-dashboard/',
    roles: ['COURIER'],
    children: [
      { title: 'Active Tasks', path: 'delivery-list' },
      { title: 'Historical Tasks', path: 'delivery-history' },
    ]
  },
];
