import api from "./axios";

export interface TechnicianRequest {
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  password?: string;
  status?: string;
  teamGroup?: string;
}

export interface TechnicianResponse {
  id?: number;
  EID: string;
  firstName: string;
  lastName: string;
  email: string;
  phone?: string;
  role?: string;
  status?: string;
  teamGroup?: string;
  createdAt?: string;
  updatedAt?: string;
}

export const createTechnician = async (
  technician: TechnicianRequest,
): Promise<TechnicianResponse> => {
  const response = await api.post("/api/technicians", technician);
  return response.data;
};

export const updateTechnician = async (
  eid: string,
  technician: TechnicianRequest,
): Promise<TechnicianResponse> => {
  const response = await api.put(`/api/technicians/${eid}`, technician);
  return response.data;
};
