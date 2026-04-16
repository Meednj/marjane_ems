export const getHello = async (): Promise<string> => {
  // Use a relative path so the Vite proxy catches it
  const response = await fetch("/api/hello");

  if (!response.ok) {
    throw new Error("Failed to fetch");
  }

  return response.text();
};
