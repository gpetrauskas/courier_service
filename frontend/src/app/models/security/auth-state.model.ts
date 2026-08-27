export interface AuthStateModel {
  isAuthenticated: boolean;
  role: string | "guest";
  name: string | null;
}
