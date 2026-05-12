import api from "./axios";

export interface Employee {
  employeeId?: string;
  id?: number;
  firstName: string;
  lastName: string;
  email: string;
  phone?: string;
  password?: string;
  departmentId?: number;
  department?: {
    departmentId?: number;
    name?: string;
  };
  status?: string;
  EID?: string;
  role?: string;
  teamGroup?: string;
}

export interface EmployeeResponse extends Employee {
  employeeId: string;
  EID: string;
  role: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface EmployeeRequest {
  firstName: string;
  lastName: string;
  email: string;
  phone?: string;
  password?: string;
  departmentId?: number;
  status?: string;
  role?: string;
  teamGroup?: string;
}

/**
 * Fetch all employees
 */
export const fetchEmployees = async (): Promise<EmployeeResponse[]> => {
  try {
    const response = await api.get("/api/employees");
    return Array.isArray(response.data) ? response.data : [];
  } catch (error) {
    console.error("Error fetching employees:", error);
    throw error;
  }
};

/**
 * Fetch employee by ID
 */
export const fetchEmployeeById = async (
  eid: string,
): Promise<EmployeeResponse> => {
  try {
    const response = await api.get(`/api/employees/${eid}`);
    return response.data;
  } catch (error) {
    console.error("Error fetching employee:", error);
    throw error;
  }
};

/**
 * Create a new employee
 */
export const createEmployee = async (
  employee: EmployeeRequest,
): Promise<EmployeeResponse> => {
  try {
    const response = await api.post("/api/employees", employee);
    return response.data;
  } catch (error) {
    console.error("Error creating employee:", error);
    if (error instanceof Error) {
      console.error("Error details:", error.message);
    }
    throw error;
  }
};

/**
 * Update an employee
 */
export const updateEmployee = async (
  eid: string,
  employee: Partial<EmployeeRequest>,
): Promise<EmployeeResponse> => {
  try {
    const response = await api.put(`/api/employees/${eid}`, employee);
    return response.data;
  } catch (error) {
    console.error("Error updating employee:", error);
    throw error;
  }
};

/**
 * Promote an existing user to technician with a team group.
 */
export const promoteEmployeeToTechnician = async (
  employeeId: number,
  teamGroup: string,
): Promise<EmployeeResponse> => {
  try {
    const response = await api.put(
      `/api/users/${employeeId}/role/technician`,
      null,
      {
        params: { teamGroup },
      },
    );
    return response.data;
  } catch (error) {
    console.error("Error promoting employee to technician:", error);
    throw error;
  }
};

/**
 * Delete an employee
 */
export const deleteEmployee = async (eid: string): Promise<void> => {
  try {
    await api.delete(`/api/employees/${eid}`);
  } catch (error) {
    console.error("Error deleting employee:", error);
    throw error;
  }
};

/**
 * Search employees by name or email
 */
export const searchEmployees = async (
  query: string,
): Promise<EmployeeResponse[]> => {
  try {
    const response = await api.get(`/api/employees/search`, {
      params: { query },
    });
    return Array.isArray(response.data) ? response.data : [];
  } catch (error) {
    console.error("Error searching employees:", error);
    throw error;
  }
};

/**
 * Get total employee count
 */
export const fetchEmployeeCount = async (): Promise<number> => {
  try {
    const response = await api.get("/api/employees/count");
    return typeof response.data === "number" ? response.data : 0;
  } catch (error) {
    console.error("Error fetching employee count:", error);
    return 0;
  }
};

/**
 * Fetch user status options from backend
 */
export const fetchUserStatuses = async (): Promise<
  Array<{ value: string; label: string }>
> => {
  try {
    const response = await api.get("/api/enums/user-statuses");
    return Array.isArray(response.data) ? response.data : [];
  } catch (error) {
    console.error("Error fetching user statuses:", error);
    return [];
  }
};

/**
 * Fetch role options from backend
 */
export const fetchRoles = async (): Promise<
  Array<{ value: string; label: string }>
> => {
  try {
    const response = await api.get("/api/enums/roles");
    return Array.isArray(response.data) ? response.data : [];
  } catch (error) {
    console.error("Error fetching roles:", error);
    return [];
  }
};
