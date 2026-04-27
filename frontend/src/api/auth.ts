import api from "./axios";

export interface AuthResponse {
  success: boolean;
  message: string;
  token?: string;
  errorCode?: string;
  role?: string;
}

export const login = async (
  eid: string,
  password: string,
): Promise<AuthResponse> => {
  const res = await api.post("api/auth/login", { eid, password });
  return res.data;
};

export const getRole = () => {
  return localStorage.getItem("role") || null;
};

export const hasRole = (role: string) => {
  return getRole() === role;
};
