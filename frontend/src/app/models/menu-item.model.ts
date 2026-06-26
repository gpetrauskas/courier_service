export interface MenuItem {
  title: string;
  path: string;
  icon?: string;
  children?: MenuItem[];
  roles?: string[];
  expandable?: boolean;
}

export type Role = 'ADMIN' | 'COURIER' | 'USER';
