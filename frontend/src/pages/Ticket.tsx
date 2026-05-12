import { useEffect, useMemo, useState } from "react";
import Navbar from "../components/Navbar";
import { getRole } from "../api/auth";
import {
  fetchTickets,
  type TicketResponse,
  updateTicketStatus,
} from "../api/ticketService";
import {
  formatEnumLabel,
  getTicketPriorityBadgeClass,
} from "../utils/ticketPriority";

const STATUS_OPTIONS = ["PENDING", "IN_PROGRESS", "RESOLVED", "CLOSED"];

const Ticket = () => {
  const [tickets, setTickets] = useState<TicketResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [userRole, setUserRole] = useState<string | null>(null);

  const [searchTerm, setSearchTerm] = useState("");
  const [statusFilter, setStatusFilter] = useState("all");

  useEffect(() => {
    const loadTickets = async () => {
      setLoading(true);
      setError(null);

      try {
        const role = getRole();
        setUserRole(role);

        if (role !== "ADMIN") {
          setError("Only administrators can view all tickets.");
          setTickets([]);
          return;
        }

        const data = await fetchTickets();
        setTickets(data);
      } catch (err) {
        console.error("Error loading all tickets:", err);
        setError("Failed to load tickets.");
        setTickets([]);
      } finally {
        setLoading(false);
      }
    };

    loadTickets();
  }, []);

  const filteredTickets = useMemo(() => {
    const term = searchTerm.trim().toLowerCase();

    return [...tickets]
      .filter((ticket) => {
        const matchesSearch =
          !term ||
          ticket.title.toLowerCase().includes(term) ||
          ticket.description.toLowerCase().includes(term) ||
          `${ticket.creator?.firstName ?? ""} ${ticket.creator?.lastName ?? ""}`
            .toLowerCase()
            .includes(term) ||
          ticket.technicians
            .map((t) => `${t.firstName} ${t.lastName}`)
            .join(" ")
            .toLowerCase()
            .includes(term);

        const matchesStatus =
          statusFilter === "all" || ticket.status === statusFilter;

        return matchesSearch && matchesStatus;
      })
      .sort((a, b) => {
        const aDate = new Date(a.updatedAt || a.createdAt).getTime();
        const bDate = new Date(b.updatedAt || b.createdAt).getTime();
        return bDate - aDate;
      });
  }, [tickets, searchTerm, statusFilter]);

  const handleStatusChange = async (ticketId: number, nextStatus: string) => {
    try {
      const updated = await updateTicketStatus(ticketId, nextStatus);
      setTickets((previous) =>
        previous.map((ticket) => (ticket.id === ticketId ? updated : ticket)),
      );
    } catch (err) {
      console.error("Error updating ticket status:", err);
      setError("Unable to update ticket status.");
    }
  };

  return (
    <div className="min-h-screen bg-linear-to-br from-slate-900 via-slate-800 to-slate-900">
      <Navbar
        userRole={userRole}
        title="All Tickets"
        subtitle="Track and monitor support tickets across the system"
      />

      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
        {error && (
          <div className="mb-6 p-5 bg-yellow-50 border-2 border-yellow-400 rounded-lg text-yellow-800">
            {error}
          </div>
        )}

        {loading ? (
          <div className="text-white text-center py-12">
            Loading all tickets...
          </div>
        ) : (
          <>
            <section className="bg-white rounded-xl shadow-lg p-5 mb-6">
              <div className="flex flex-col md:flex-row gap-4 md:items-center md:justify-between">
                <p className="text-slate-700 font-medium">
                  Total tickets: {filteredTickets.length}
                </p>
                <div className="flex gap-3">
                  <input
                    type="text"
                    placeholder="Search tickets..."
                    value={searchTerm}
                    onChange={(event) => setSearchTerm(event.target.value)}
                    className="px-3 py-2 border border-slate-300 rounded-lg focus:outline-none focus:border-indigo-500"
                  />
                  <select
                    value={statusFilter}
                    onChange={(event) => setStatusFilter(event.target.value)}
                    className="px-3 py-2 border border-slate-300 rounded-lg focus:outline-none focus:border-indigo-500"
                  >
                    <option value="all">All Statuses</option>
                    {STATUS_OPTIONS.map((status) => (
                      <option key={status} value={status}>
                        {formatEnumLabel(status)}
                      </option>
                    ))}
                  </select>
                </div>
              </div>
            </section>

            <section className="bg-white rounded-xl shadow-lg overflow-x-auto">
              <table className="w-full text-sm">
                <thead className="bg-slate-100 text-slate-700">
                  <tr>
                    <th className="text-left px-4 py-3">Ticket</th>
                    <th className="text-left px-4 py-3">Creator</th>
                    <th className="text-left px-4 py-3">Technician</th>
                    <th className="text-left px-4 py-3">Category</th>
                    <th className="text-left px-4 py-3">Priority</th>
                    <th className="text-left px-4 py-3">Status</th>
                    <th className="text-left px-4 py-3">Updated</th>
                  </tr>
                </thead>
                <tbody>
                  {filteredTickets.length === 0 ? (
                    <tr>
                      <td
                        colSpan={7}
                        className="text-center py-10 text-slate-500"
                      >
                        No tickets found.
                      </td>
                    </tr>
                  ) : (
                    filteredTickets.map((ticket) => (
                      <tr
                        key={ticket.id}
                        className="border-b border-slate-100 hover:bg-slate-50"
                      >
                        <td className="px-4 py-3">
                          <p className="font-semibold text-slate-900">
                            #{ticket.id} - {ticket.title}
                          </p>
                          <p className="text-xs text-slate-500 mt-1 line-clamp-2">
                            {ticket.description}
                          </p>
                        </td>
                        <td className="px-4 py-3 text-slate-700">
                          {ticket.creator
                            ? `${ticket.creator.firstName} ${ticket.creator.lastName}`
                            : "-"}
                        </td>
                        <td className="px-4 py-3 text-slate-700">
                          {ticket.technicians && ticket.technicians.length > 0
                            ? ticket.technicians
                                .map((t) => `${t.firstName} ${t.lastName}`)
                                .join(", ")
                            : "Unassigned"}
                        </td>
                        <td className="px-4 py-3">
                          {formatEnumLabel(ticket.category)}
                        </td>
                        <td className="px-4 py-3">
                          <span
                            className={`inline-flex items-center rounded-full px-3 py-1 text-xs font-semibold uppercase tracking-wide ${getTicketPriorityBadgeClass(ticket.priority)}`}
                          >
                            {formatEnumLabel(ticket.priority)}
                          </span>
                        </td>
                        <td className="px-4 py-3">
                          <select
                            value={ticket.status}
                            onChange={(event) =>
                              handleStatusChange(ticket.id, event.target.value)
                            }
                            className="px-2 py-1 border border-slate-300 rounded-md bg-white"
                          >
                            {STATUS_OPTIONS.map((status) => (
                              <option key={status} value={status}>
                                {formatEnumLabel(status)}
                              </option>
                            ))}
                          </select>
                        </td>
                        <td className="px-4 py-3 text-slate-700">
                          {ticket.updatedAt
                            ? new Date(ticket.updatedAt).toLocaleString()
                            : ticket.createdAt
                              ? new Date(ticket.createdAt).toLocaleString()
                              : "-"}
                        </td>
                      </tr>
                    ))
                  )}
                </tbody>
              </table>
            </section>
          </>
        )}
      </main>
    </div>
  );
};

export default Ticket;
