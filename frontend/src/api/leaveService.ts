import api from "./axios";

export interface LeaveResponse {
  id: number;
  EID: string;
  user: {
    id: number;
    EID: string;
    firstName: string;
    lastName: string;
    email: string;
  };
  approver?: {
    id: number;
    EID: string;
    firstName: string;
    lastName: string;
    email: string;
  };
  startDate: string;
  endDate: string;
  type: string;
  subject: string;
  status: string;
}

export interface LeaveRequest {
  userId: number;
  approverId?: number;
  startDate: string;
  endDate: string;
  type: string;
  subject?: string;
  status?: string;
}

/**
 * Fetch all leave requests
 */
export const fetchLeaves = async (): Promise<LeaveResponse[]> => {
  try {
    const response = await api.get("/api/leaves");
    return Array.isArray(response.data) ? response.data : [];
  } catch (error) {
    console.error("Error fetching leaves:", error);
    throw error;
  }
};

/**
 * Fetch leave by ID
 */
export const fetchLeaveById = async (id: number): Promise<LeaveResponse> => {
  try {
    const response = await api.get(`/api/leaves/${id}`);
    return response.data;
  } catch (error) {
    console.error("Error fetching leave:", error);
    throw error;
  }
};

/**
 * Create a new leave request
 */
export const createLeave = async (
  leave: LeaveRequest,
): Promise<LeaveResponse> => {
  try {
    const response = await api.post("/api/leaves", leave);
    return response.data;
  } catch (error) {
    console.error("Error creating leave:", error);
    if (error instanceof Error) {
      console.error("Error details:", error.message);
    }
    throw error;
  }
};

/**
 * Update a leave request
 */
export const updateLeave = async (
  id: number,
  leave: Partial<LeaveRequest>,
): Promise<LeaveResponse> => {
  try {
    const response = await api.put(`/api/leaves/${id}`, leave);
    return response.data;
  } catch (error) {
    console.error("Error updating leave:", error);
    throw error;
  }
};

/**
 * Delete a leave request
 */
export const deleteLeave = async (id: number): Promise<void> => {
  try {
    await api.delete(`/api/leaves/${id}`);
  } catch (error) {
    console.error("Error deleting leave:", error);
    throw error;
  }
};

/**
 * Get leave requests for a specific user
 */
export const fetchLeavesByUser = async (
  userId: number,
): Promise<LeaveResponse[]> => {
  try {
    const response = await api.get(`/api/leaves/user/${userId}`);
    return Array.isArray(response.data) ? response.data : [];
  } catch (error) {
    console.error("Error fetching user leaves:", error);
    throw error;
  }
};

/**
 * Get pending leave requests
 */
export const fetchPendingLeaves = async (): Promise<LeaveResponse[]> => {
  try {
    const response = await api.get("/api/leaves/pending");
    return Array.isArray(response.data) ? response.data : [];
  } catch (error) {
    console.error("Error fetching pending leaves:", error);
    throw error;
  }
};

/**
 * Approve a leave request
 */
export const approveLeave = async (
  leaveId: number,
  approverId: number,
): Promise<LeaveResponse> => {
  try {
    const response = await api.post(
      `/api/leaves/${leaveId}/approve/${approverId}`,
    );
    return response.data;
  } catch (error) {
    console.error("Error approving leave:", error);
    throw error;
  }
};

/**
 * Reject a leave request
 */
export const rejectLeave = async (
  leaveId: number,
  approverId: number,
): Promise<LeaveResponse> => {
  try {
    const response = await api.post(
      `/api/leaves/${leaveId}/reject/${approverId}`,
    );
    return response.data;
  } catch (error) {
    console.error("Error rejecting leave:", error);
    throw error;
  }
};

/**
 * Get leave types
 */
export const getLeaveTypes = (): Array<{ value: string; label: string }> => {
  return [
    { value: "ANNUAL", label: "Annual Leave" },
    { value: "MATERNITY", label: "Maternity Leave" },
    { value: "PATERNITY", label: "Paternity Leave" },
    { value: "SICK", label: "Sick Leave" },
  ];
};

/**
 * Get leave statuses
 */
export const getLeaveStatuses = (): Array<{ value: string; label: string }> => {
  return [
    { value: "PENDING", label: "Pending" },
    { value: "APPROVED", label: "Approved" },
    { value: "REJECTED", label: "Rejected" },
  ];
};
