import { UserRole } from "../../enums/user-role.enum";

export interface UserFilterModel {
  kind: 'user';
  userRole: UserRole;
  keyword: string;
}
