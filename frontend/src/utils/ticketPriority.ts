export const TICKET_PRIORITY_OPTIONS = ["LOW", "MEDIUM", "URGENT"] as const;

export const formatEnumLabel = (value: string | null | undefined) => {
  if (!value) {
    return "-";
  }

  return value
    .replaceAll("_", " ")
    .toLowerCase()
    .replace(/\b\w/g, (char) => char.toUpperCase());
};

export const getTicketPriorityBadgeClass = (
  priority: string | null | undefined,
) => {
  switch ((priority ?? "").toUpperCase()) {
    case "LOW":
      return "bg-emerald-100 text-emerald-800 ring-1 ring-emerald-200";
    case "MEDIUM":
      return "bg-amber-100 text-amber-800 ring-1 ring-amber-200";
    case "URGENT":
      return "bg-rose-100 text-rose-800 ring-1 ring-rose-200";
    default:
      return "bg-slate-100 text-slate-700 ring-1 ring-slate-200";
  }
};
