export interface JwtPayload {
  id: number;
  role: string;
  name: string;
  sub: string;
  exp: number;
  iat: number;
}
