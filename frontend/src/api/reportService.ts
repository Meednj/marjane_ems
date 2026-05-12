import api from "./axios";

export interface ReportMetrics {
  usersCount: number;
  ticketsCount: number;
  pendingTickets: number;
  departmentsCount: number;
  totalLeaves: number;
  pendingLeaves: number;
  resolvedTickets: number;
}

const toCount = (value: unknown): number => {
  if (typeof value === "number") {
    return value;
  }

  if (Array.isArray(value)) {
    return value.length;
  }

  return 0;
};

const settledCount = async (promise: Promise<unknown>) => {
  try {
    const response = await promise;
    return toCount((response as { data?: unknown }).data);
  } catch (error) {
    console.error("Error loading report metric:", error);
    return 0;
  }
};

export const fetchReportMetrics = async (): Promise<ReportMetrics> => {
  const [
    usersCount,
    ticketsCount,
    pendingTickets,
    departmentsCount,
    totalLeaves,
    pendingLeaves,
    resolvedTickets,
  ] = await Promise.all([
    settledCount(api.get("/api/users/count")),
    settledCount(api.get("/api/tickets/count")),
    settledCount(api.get("/api/tickets/count/status/PENDING")),
    settledCount(api.get("/api/departments/count")),
    settledCount(api.get("/api/leaves")),
    settledCount(api.get("/api/leaves/pending")),
    settledCount(api.get("/api/tickets/status/RESOLVED")),
  ]);

  return {
    usersCount,
    ticketsCount,
    pendingTickets,
    departmentsCount,
    totalLeaves,
    pendingLeaves,
    resolvedTickets,
  };
};
