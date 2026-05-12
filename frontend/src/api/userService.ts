import api from "./axios";

export const updateUserStatus = async (
  status: "ACTIVE" | "INACTIVE",
): Promise<void> => {
  await api.put(`/api/users/me/status/${status}`);
};

export const getCurrentUser = async () => {
  const res = await api.get(`/api/users/me`);
  return res.data;
};
