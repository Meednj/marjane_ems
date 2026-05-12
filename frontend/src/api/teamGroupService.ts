import api from "./axios";

export interface TechnicianOption {
  id: number;
  EID: string;
  firstName: string;
  lastName: string;
  email: string;
  phone?: string;
  role?: string;
  status?: string;
  teamGroup?: string;
}

export interface TeamGroupResponse {
  id: number;
  name: string;
  numberOfMembers: number;
  technicians: TechnicianOption[];
}

export const TEAM_GROUP_OPTIONS = [
  "TS_IT",
  "STOCK",
  "RH",
  "SECURITY",
  "LOGISTICS",
  "OTHER",
];

export const fetchTeamGroups = async (): Promise<TeamGroupResponse[]> => {
  try {
    const response = await api.get("/api/team-groups");
    return Array.isArray(response.data) ? response.data : [];
  } catch (error) {
    console.error("Error fetching team groups:", error);
    return [];
  }
};

export const fetchTechniciansByCategory = async (
  category: string,
): Promise<TechnicianOption[]> => {
  try {
    const response = await api.get(
      `/api/team-groups/category/${category}/technicians`,
    );
    return Array.isArray(response.data) ? response.data : [];
  } catch (error) {
    console.error("Error fetching technicians by category:", error);
    return [];
  }
};
