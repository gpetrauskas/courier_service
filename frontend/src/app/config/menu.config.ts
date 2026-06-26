import { MenuItem, Role } from '../models/menu-item.model';
import { courierMenu } from './courier-menu.config';
import { adminMenu } from './admin-menu.config';
import { userMenu } from './user-menu.config';

export const menuConfig: Record<Role, MenuItem[]> = {
  ADMIN: adminMenu,
  COURIER: courierMenu,
  USER: userMenu,
}
