import api from "./axios";

export interface SimpleUserResponse {
  id: number;
  EID: string;
  lastName: string;
  firstName: string;
}

export interface TicketResponse {
  id: number;
  creator: SimpleUserResponse | null;
  technicians: SimpleUserResponse[];
  title: string;
  description: string;
  category: string;
  priority: string;
  status: string;
  createdAt: string;
  updatedAt: string;
  resolvedAt?: string;
}

export interface TicketRequest {
  creatorId: number;
  technicianId?: number;
  title: string;
  description: string;
  category: string;
  priority: string;
  status?: string;
}

const normalizeTicket = (ticket: unknown): TicketResponse => {
  const data = (ticket ?? {}) as Record<string, unknown>;

  const creator = (data.creator ?? null) as SimpleUserResponse | null;
  const technicians = Array.isArray(data.technicians)
    ? (data.technicians as SimpleUserResponse[])
    : [];

  return {
    id: Number(data.id ?? 0),
    creator,
    technicians,
    title: String(data.title ?? ""),
    description: String(data.description ?? ""),
    category: String(data.category ?? "OTHER"),
    priority: String(data.priority ?? "LOW"),
    status: String(data.status ?? "PENDING"),
    createdAt: String(data.createdAt ?? ""),
    updatedAt: String(data.updatedAt ?? ""),
    resolvedAt: data.resolvedAt ? String(data.resolvedAt) : undefined,
  };
};

const normalizeTicketList = (payload: unknown): TicketResponse[] => {
  if (!Array.isArray(payload)) {
    return [];
  }

  return payload.map((item) => normalizeTicket(item));
};

export const fetchTickets = async (): Promise<TicketResponse[]> => {
  try {
    const response = await api.get("/api/tickets");
    return normalizeTicketList(response.data);
  } catch (error) {
    console.error("Error fetching tickets:", error);
    throw error;
  }
};

export const fetchTicketsByCreator = async (
  creatorId: number,
): Promise<TicketResponse[]> => {
  try {
    const response = await api.get(`/api/tickets/creator/${creatorId}`);
    return normalizeTicketList(response.data);
  } catch (error) {
    console.error("Error fetching tickets by creator:", error);
    throw error;
  }
};

export const fetchTicketsByTechnician = async (
  technicianId: number,
): Promise<TicketResponse[]> => {
  try {
    const response = await api.get(`/api/tickets/technician/${technicianId}`);
    return normalizeTicketList(response.data);
  } catch (error) {
    console.error("Error fetching tickets by technician:", error);
    throw error;
  }
};

export const fetchUnassignedTickets = async (): Promise<TicketResponse[]> => {
  try {
    const response = await api.get(`/api/tickets/unassigned`);
    return normalizeTicketList(response.data);
  } catch (error) {
    console.error("Error fetching unassigned tickets:", error);
    throw error;
  }
};

export const createTicket = async (
  request: TicketRequest,
): Promise<TicketResponse> => {
  const res = await api.post("/api/tickets", request);
  return normalizeTicket(res.data);
};

export const updateTicket = async (
  id: number,
  request: TicketRequest,
): Promise<TicketResponse> => {
  const res = await api.put(`/api/tickets/${id}`, request);
  return normalizeTicket(res.data);
};

export const assignTicket = async (
  ticketId: number,
  technicianId: number,
): Promise<TicketResponse> => {
  const res = await api.post(`/api/tickets/${ticketId}/assign/${technicianId}`);
  return normalizeTicket(res.data);
};

export const updateTicketStatus = async (
  ticketId: number,
  status: string,
): Promise<TicketResponse> => {
  const res = await api.put(`/api/tickets/${ticketId}/status/${status}`);
  return normalizeTicket(res.data);
};

export const deleteTicket = async (id: number): Promise<void> => {
  await api.delete(`/api/tickets/${id}`);
};
