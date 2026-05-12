import api from "./axios";
import { fetchLeaves, fetchPendingLeaves } from "./leaveService";

export interface DashboardData {
  totalUsers: number;
  totalTickets: number;
  pendingTickets: number;
  totalEmployees?: number;
  departmentsCount?: number;
  totalLeaves?: number;
  pendingLeaves?: number;
  totalTechnicians?: number;
  totalAdministrators?: number;
  availableTechnicians?: number;
}

export interface Ticket {
  id?: number;
  status?: string;
  priority?: string;
  category?: string;
  [key: string]: any;
}

/**
 * Fetch dashboard statistics
 * Currently fetches: users count, tickets count, and open tickets
 * TODO: Add more metrics later
 */
export const fetchDashboardStats = async (): Promise<DashboardData> => {
  try {
    const [
      usersCountRes,
      ticketsCountRes,
      pendingTicketsRes,
      departmentsCountRes,
    ] = await Promise.all([
      fetchUserCount(),
      fetchTicketsCount(),
      fetchPendingTickets(),
      fetchDepartmentsCount(),
    ]);

    const [totalLeavesRes, pendingLeavesRes] = await Promise.all([
      fetchLeaves()
        .then((leaves) => leaves.length)
        .catch(() => 0),
      fetchPendingLeaves()
        .then((leaves) => leaves.length)
        .catch(() => 0),
    ]);

    return {
      totalUsers: usersCountRes,
      totalTickets: ticketsCountRes,
      pendingTickets: pendingTicketsRes,
      departmentsCount: departmentsCountRes,
      totalLeaves: totalLeavesRes,
      pendingLeaves: pendingLeavesRes,
    };
  } catch (error) {
    console.error("Error fetching dashboard stats:", error);
    throw error;
  }
};

/**
 * Fetch total user count
 */
export const fetchUserCount = async (): Promise<number> => {
  try {
    const response = await api.get("api/users/count");
    return response.data || 0;
  } catch (error) {
    console.error("Error fetching user count:", error);
    return 0;
  }
};

/**
 * Fetch total tickets count
 */
export const fetchTicketsCount = async (): Promise<number> => {
  try {
    const response = await api.get("api/tickets/count");
    const data = response.data;

    return typeof data === "number"
      ? data
      : Array.isArray(data)
        ? data.length
        : 0;
  } catch (error) {
    console.error("Error fetching tickets count:", error);
    return 0;
  }
};

export const fetchPendingTickets = async (): Promise<number> => {
  try {
    const response = await api.get("/api/tickets/count/status/PENDING");
    return response.data || 0;
  } catch (error) {
    console.error("Error fetching pending tickets:", error);
    return 0;
  }
};

export const fetchDepartmentsCount = async (): Promise<number> => {
  try {
    const response = await api.get("api/departments/count");
    return response.data || 0;
  } catch (error) {
    console.error("Error fetching departments count:", error);
    return 0;
  }
};
