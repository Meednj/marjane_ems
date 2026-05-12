import api from "./axios";

export interface DepartmentPayload {
  name: string;
  description: string;
}

export interface DepartmentResponse {
  departmentId?: number;
  name: string;
  description: string;
}

export const fetchDepartmentCount = async (): Promise<number> => {
  try {
    const response = await api.get("/api/departments/count");
    return typeof response.data === "number" ? response.data : 0;
  } catch (error) {
    console.error("Error fetching department count:", error);
    return 0;
  }
};

export const fetchDepartments = async (): Promise<DepartmentResponse[]> => {
  try {
    const response = await api.get("/api/departments");
    return response.data;
  } catch (error) {
    console.error("Error fetching departments:", error);
    return [];
  }
};

export const fetchDepartmentById = async (
  id: number,
): Promise<DepartmentResponse> => {
  try {
    const response = await api.get(`/api/departments/${id}`);
    return response.data;
  } catch (error) {
    console.error("Error fetching department:", error);
    throw error;
  }
};

export const createDepartment = async (
  payload: DepartmentPayload,
): Promise<DepartmentResponse> => {
  const response = await api.post("/api/departments", payload);
  return response.data;
};

export const updateDepartment = async (
  id: number,
  payload: Partial<DepartmentPayload>,
): Promise<DepartmentResponse> => {
  const response = await api.put(`/api/departments/${id}`, payload);
  return response.data;
};

export const deleteDepartment = async (id: number): Promise<void> => {
  await api.delete(`/api/departments/${id}`);
};
