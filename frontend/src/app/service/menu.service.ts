import { Injectable } from '@angular/core';
import { MenuItem, Role } from '../models/menu-item.model';
import { menuConfig } from '../config/menu.config';

@Injectable({
  providedIn: 'root'
})
export class MenuService {
  getMenuItems(role: Role): MenuItem[] {
    const roleMenu = menuConfig[role] || [];
    return roleMenu.filter(i => !i.roles || i.roles.includes(role));
  }
}
