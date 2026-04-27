import api from "./axios";

export interface DashboardData {
  totalUsers: number;
  totalTickets: number;
  openTickets: number;
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
    const [usersCountRes, ticketsCountRes, openTicketsRes] = await Promise.all([
      fetchUserCount(),
      fetchTicketsCount(),
      fetchOpenTickets(),
    ]);

    return {
      totalUsers: usersCountRes,
      totalTickets: ticketsCountRes,
      openTickets: openTicketsRes,
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

/**
 * Fetch open tickets count
 */
export const fetchOpenTickets = async (): Promise<number> => {
  try {
    const response = await api.get("api/tickets/count/status/PENDING");
    const data = response.data;
    return Array.isArray(data) ? data.length : data || 0;
  } catch (error) {
    console.error("Error fetching open tickets:", error);
    return 0;
  }
};

