import { useEffect, useMemo, useState } from "react";
import Navbar from "../components/Navbar";
import { getRole } from "../api/auth";
import {
  createTicket,
  fetchTickets,
  type TicketRequest,
  type TicketResponse,
  updateTicket,
  updateTicketStatus,
} from "../api/ticketService";
import { fetchEmployees, type EmployeeResponse } from "../api/employeeService";
// teamGroupService not required here; assignment is automatic
import {
  formatEnumLabel,
  getTicketPriorityBadgeClass,
  TICKET_PRIORITY_OPTIONS,
} from "../utils/ticketPriority";

const STATUS_OPTIONS = ["PENDING", "IN_PROGRESS", "RESOLVED", "CLOSED"];
const PRIORITY_OPTIONS = TICKET_PRIORITY_OPTIONS;
const CATEGORY_OPTIONS = [
  "TS_IT",
  "STOCK",
  "RH",
  "SECURITY",
  "LOGISTICS",
  "OTHER",
];

const getCurrentEidFromToken = () => {
  const token = localStorage.getItem("token");
  if (!token) {
    return null;
  }

  try {
    const payloadPart = token.split(".")[1];
    if (!payloadPart) {
      return null;
    }

    const base64 = payloadPart.replace(/-/g, "+").replace(/_/g, "/");
    const padded = base64.padEnd(Math.ceil(base64.length / 4) * 4, "=");
    const decoded = atob(padded);
    const parsed = JSON.parse(decoded) as { sub?: string };
    return parsed.sub ?? null;
  } catch {
    return null;
  }
};

const TicketAdmin = () => {
  const [tickets, setTickets] = useState<TicketResponse[]>([]);
  const [employees, setEmployees] = useState<EmployeeResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);

  const [error, setError] = useState<string | null>(null);
  const [userRole, setUserRole] = useState<string | null>(null);
  const [currentUserId, setCurrentUserId] = useState<number | null>(null);
  const [currentUserEid, setCurrentUserEid] = useState<string | null>(null);

  const [searchTerm, setSearchTerm] = useState("");
  const [statusFilter, setStatusFilter] = useState("all");
  const [priorityFilter, setPriorityFilter] = useState("all");
  const [categoryFilter, setCategoryFilter] = useState("all");

  const [showFormModal, setShowFormModal] = useState(false);
  const [formMode, setFormMode] = useState<"create" | "edit">("create");
  const [selectedTicket, setSelectedTicket] = useState<TicketResponse | null>(
    null,
  );

  const [ticketForm, setTicketForm] = useState({
    title: "",
    description: "",
    category: "TS_IT",
    priority: "MEDIUM",
    technicianId: "",
  });

  const refreshTickets = async () => {
    const data = await fetchTickets();
    setTickets(data);
  };

  useEffect(() => {
    const loadData = async () => {
      setLoading(true);
      setError(null);

      try {
        const role = getRole();
        setUserRole(role);

        const eidFromToken = getCurrentEidFromToken();
        setCurrentUserEid(eidFromToken);

        const [ticketsResult, employeesResult] = await Promise.allSettled([
          fetchTickets(),
          fetchEmployees(),
        ]);

        if (ticketsResult.status === "rejected") {
          throw ticketsResult.reason;
        }

        const loadedTickets = ticketsResult.value;
        setTickets(loadedTickets);

        const employeeList =
          employeesResult.status === "fulfilled" ? employeesResult.value : [];
        setEmployees(employeeList);

        const matchedEmployee = employeeList.find(
          (employee) => employee.EID === eidFromToken,
        );

        const userIdFromTicket = loadedTickets.find(
          (ticket) => ticket.creator?.EID === eidFromToken,
        )?.creator?.id;

        setCurrentUserId(matchedEmployee?.id ?? userIdFromTicket ?? null);
      } catch (err) {
        console.error("Error loading tickets:", err);
        setError("Failed to load tickets. Please refresh and try again.");
        setTickets([]);
        setEmployees([]);
      } finally {
        setLoading(false);
      }
    };

    loadData();
  }, []);

  // assignment is automatic; no need to load technicians here

  const myTickets = useMemo(() => {
    if (!currentUserEid && !currentUserId) {
      return [];
    }

    return tickets.filter((ticket) => {
      const isCreatorById = currentUserId
        ? ticket.creator?.id === currentUserId
        : false;
      const isCreatorByEid = currentUserEid
        ? ticket.creator?.EID === currentUserEid
        : false;
      const isTechnicianById = currentUserId
        ? ticket.technicians?.some((t) => t.id === currentUserId)
        : false;
      const isTechnicianByEid = currentUserEid
        ? ticket.technicians?.some((t) => t.EID === currentUserEid)
        : false;

      return (
        isCreatorById || isCreatorByEid || isTechnicianById || isTechnicianByEid
      );
    });
  }, [tickets, currentUserEid, currentUserId]);

  const filteredTickets = useMemo(() => {
    const term = searchTerm.trim().toLowerCase();

    return [...myTickets]
      .filter((ticket) => {
        const matchesSearch =
          !term ||
          ticket.title.toLowerCase().includes(term) ||
          ticket.description.toLowerCase().includes(term) ||
          `${ticket.creator?.firstName ?? ""} ${ticket.creator?.lastName ?? ""}`
            .toLowerCase()
            .includes(term);

        const matchesStatus =
          statusFilter === "all" || ticket.status === statusFilter;
        const matchesPriority =
          priorityFilter === "all" || ticket.priority === priorityFilter;
        const matchesCategory =
          categoryFilter === "all" || ticket.category === categoryFilter;

        return (
          matchesSearch && matchesStatus && matchesPriority && matchesCategory
        );
      })
      .sort((a, b) => {
        const aDate = new Date(a.updatedAt || a.createdAt).getTime();
        const bDate = new Date(b.updatedAt || b.createdAt).getTime();
        return bDate - aDate;
      });
  }, [myTickets, searchTerm, statusFilter, priorityFilter, categoryFilter]);

  const openCreateModal = () => {
    setFormMode("create");
    setSelectedTicket(null);
    setTicketForm({
      title: "",
      description: "",
      category: "TS_IT",
      priority: "MEDIUM",
      technicianId: "",
    });
    setShowFormModal(true);
  };

  const openEditModal = (ticket: TicketResponse) => {
    setFormMode("edit");
    setSelectedTicket(ticket);
    setTicketForm({
      title: ticket.title,
      description: ticket.description,
      category: ticket.category,
      priority: ticket.priority,
      technicianId: "",
    });
    setShowFormModal(true);
  };

  const closeFormModal = () => {
    setShowFormModal(false);
    setSelectedTicket(null);
  };

  const handleSubmitForm = async (event: React.FormEvent) => {
    event.preventDefault();
    setSubmitting(true);
    setError(null);

    try {
      if (formMode === "create") {
        if (!currentUserId) {
          throw new Error(
            "Creator ID was not resolved from the logged-in user.",
          );
        }

        const payload: TicketRequest = {
          creatorId: currentUserId,
          title: ticketForm.title.trim(),
          description: ticketForm.description.trim(),
          category: ticketForm.category,
          priority: ticketForm.priority,
        };

        await createTicket(payload);
      }

      if (formMode === "edit" && selectedTicket) {
        if (!selectedTicket.creator?.id) {
          throw new Error("Selected ticket has no valid creator.");
        }

        const payload: TicketRequest = {
          creatorId: selectedTicket.creator.id,
          title: ticketForm.title.trim(),
          description: ticketForm.description.trim(),
          category: ticketForm.category,
          priority: ticketForm.priority,
          status: selectedTicket.status,
        };

        await updateTicket(selectedTicket.id, payload);
      }

      await refreshTickets();
      closeFormModal();
    } catch (err) {
      console.error("Error saving ticket:", err);
      setError(
        "Unable to save ticket. Please verify your input and try again.",
      );
    } finally {
      setSubmitting(false);
    }
  };

  const handleStatusChange = async (ticketId: number, nextStatus: string) => {
    try {
      const updated = await updateTicketStatus(ticketId, nextStatus);
      if (nextStatus === "CLOSED") {
        setTickets((previous) =>
          previous.filter((ticket) => ticket.id !== ticketId),
        );
        return;
      }

      setTickets((previous) =>
        previous.map((ticket) => (ticket.id === ticketId ? updated : ticket)),
      );
    } catch (err) {
      console.error("Error updating status:", err);
      setError("Unable to update ticket status.");
    }
  };

  const totals = useMemo(
    () => ({
      total: myTickets.length,
      pending: myTickets.filter((ticket) => ticket.status === "PENDING").length,
      inProgress: myTickets.filter((ticket) => ticket.status === "IN_PROGRESS")
        .length,
      resolved: myTickets.filter((ticket) => ticket.status === "RESOLVED")
        .length,
    }),
    [myTickets],
  );

  return (
    <div className="min-h-screen bg-linear-to-br from-slate-900 via-slate-800 to-slate-900">
      <Navbar
        userRole={userRole}
        title="Ticket Management"
        subtitle="Create and track tickets with latest updates first"
      />

      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
        {error && (
          <div className="mb-6 p-5 bg-yellow-50 border-2 border-yellow-400 rounded-lg flex items-start gap-4">
            <div>
              <p className="text-sm font-semibold text-yellow-900 mb-1">
                Notice
              </p>
              <p className="text-sm text-yellow-800">{error}</p>
            </div>
          </div>
        )}

        {loading ? (
          <div className="flex items-center justify-center py-16">
            <div className="text-center">
              <div className="inline-block animate-spin rounded-full h-12 w-12 border-b-2 border-indigo-500 mb-4" />
              <p className="text-white">Loading your tickets...</p>
            </div>
          </div>
        ) : (
          <>
            <section className="grid grid-cols-1 md:grid-cols-4 gap-4 mb-8">
              <div className="bg-white rounded-lg p-4 shadow-lg">
                <p className="text-xs uppercase tracking-wide text-gray-500">
                  My Tickets
                </p>
                <p className="text-2xl font-bold text-slate-900">
                  {totals.total}
                </p>
              </div>
              <div className="bg-white rounded-lg p-4 shadow-lg">
                <p className="text-xs uppercase tracking-wide text-gray-500">
                  Pending
                </p>
                <p className="text-2xl font-bold text-amber-600">
                  {totals.pending}
                </p>
              </div>
              <div className="bg-white rounded-lg p-4 shadow-lg">
                <p className="text-xs uppercase tracking-wide text-gray-500">
                  In Progress
                </p>
                <p className="text-2xl font-bold text-blue-600">
                  {totals.inProgress}
                </p>
              </div>
              <div className="bg-white rounded-lg p-4 shadow-lg">
                <p className="text-xs uppercase tracking-wide text-gray-500">
                  Resolved
                </p>
                <p className="text-2xl font-bold text-green-600">
                  {totals.resolved}
                </p>
              </div>
            </section>

            <section className="bg-white rounded-xl shadow-lg p-5 mb-6">
              <div className="flex flex-col gap-4 lg:flex-row lg:items-center lg:justify-between">
                <p className="text-slate-700 font-medium">
                  Your tickets are sorted by latest update date.
                </p>

                <button
                  onClick={openCreateModal}
                  disabled={
                    employees.filter((employee) => employee.role === "EMPLOYEE")
                      .length === 0
                  }
                  className="bg-indigo-600 hover:bg-indigo-700 disabled:opacity-60 disabled:cursor-not-allowed text-white font-bold py-2.5 px-5 rounded-lg transition-colors"
                  title="Create new ticket"
                >
                  + New Ticket
                </button>
              </div>

              <div className="grid grid-cols-1 md:grid-cols-4 gap-3 mt-4">
                <input
                  type="text"
                  value={searchTerm}
                  onChange={(event) => setSearchTerm(event.target.value)}
                  placeholder="Search title, description..."
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
                <select
                  value={priorityFilter}
                  onChange={(event) => setPriorityFilter(event.target.value)}
                  className="px-3 py-2 border border-slate-300 rounded-lg focus:outline-none focus:border-indigo-500"
                >
                  <option value="all">All Priorities</option>
                  {PRIORITY_OPTIONS.map((priority) => (
                    <option key={priority} value={priority}>
                      {formatEnumLabel(priority)}
                    </option>
                  ))}
                </select>
                <select
                  value={categoryFilter}
                  onChange={(event) => setCategoryFilter(event.target.value)}
                  className="px-3 py-2 border border-slate-300 rounded-lg focus:outline-none focus:border-indigo-500"
                >
                  <option value="all">All Categories</option>
                  {CATEGORY_OPTIONS.map((category) => (
                    <option key={category} value={category}>
                      {formatEnumLabel(category)}
                    </option>
                  ))}
                </select>
              </div>
            </section>

            <section className="bg-white rounded-xl shadow-lg overflow-x-auto">
              <table className="w-full text-sm">
                <thead className="bg-slate-100 text-slate-700">
                  <tr>
                    <th className="text-left px-4 py-3">Ticket</th>
                    <th className="text-left px-4 py-3">Creator</th>
                    <th className="text-left px-4 py-3">Category</th>
                    <th className="text-left px-4 py-3">Priority</th>
                    <th className="text-left px-4 py-3">Status</th>
                    <th className="text-left px-4 py-3">Updated</th>
                    <th className="text-left px-4 py-3">Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {filteredTickets.length === 0 ? (
                    <tr>
                      <td
                        colSpan={7}
                        className="text-center py-10 text-slate-500"
                      >
                        No tickets match the current filters.
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
                        <td className="px-4 py-3">
                          <button
                            onClick={() => openEditModal(ticket)}
                            className="px-2.5 py-1.5 text-xs font-semibold rounded-md bg-slate-200 text-slate-700 hover:bg-slate-300"
                          >
                            Edit
                          </button>
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

      {showFormModal && (
        <div className="fixed inset-0 z-50 bg-black/50 flex items-center justify-center p-4">
          <div className="w-full max-w-2xl bg-white rounded-xl shadow-2xl">
            <div className="px-6 py-4 border-b border-slate-200 flex items-center justify-between">
              <h3 className="text-lg font-bold text-slate-900">
                {formMode === "create" ? "Create Ticket" : "Edit Ticket"}
              </h3>
              <button
                onClick={closeFormModal}
                className="text-slate-500 hover:text-slate-800"
              >
                Close
              </button>
            </div>

            <form onSubmit={handleSubmitForm} className="p-6 space-y-4">
              <div>
                <label className="block text-sm font-semibold text-slate-700 mb-1">
                  Title
                </label>
                <input
                  required
                  value={ticketForm.title}
                  onChange={(event) =>
                    setTicketForm((previous) => ({
                      ...previous,
                      title: event.target.value,
                    }))
                  }
                  className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:outline-none focus:border-indigo-500"
                />
              </div>

              <div>
                <label className="block text-sm font-semibold text-slate-700 mb-1">
                  Description
                </label>
                <textarea
                  required
                  rows={4}
                  value={ticketForm.description}
                  onChange={(event) =>
                    setTicketForm((previous) => ({
                      ...previous,
                      description: event.target.value,
                    }))
                  }
                  className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:outline-none focus:border-indigo-500"
                />
              </div>

              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-semibold text-slate-700 mb-1">
                    Category
                  </label>
                  <select
                    value={ticketForm.category}
                    onChange={(event) =>
                      setTicketForm((previous) => ({
                        ...previous,
                        category: event.target.value,
                        technicianId: "",
                      }))
                    }
                    className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:outline-none focus:border-indigo-500"
                  >
                    {CATEGORY_OPTIONS.map((category) => (
                      <option key={category} value={category}>
                        {formatEnumLabel(category)}
                      </option>
                    ))}
                  </select>
                </div>

                <div>
                  <label className="block text-sm font-semibold text-slate-700 mb-1">
                    Priority
                  </label>
                  <select
                    value={ticketForm.priority}
                    onChange={(event) =>
                      setTicketForm((previous) => ({
                        ...previous,
                        priority: event.target.value,
                      }))
                    }
                    className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:outline-none focus:border-indigo-500"
                  >
                    {PRIORITY_OPTIONS.map((priority) => (
                      <option key={priority} value={priority}>
                        {formatEnumLabel(priority)}
                      </option>
                    ))}
                  </select>
                </div>

                <div className="md:col-span-2">
                  <p className="text-sm text-slate-700">
                    Assignment is automatic: technicians in the selected
                    category's team group will receive the ticket.
                  </p>
                </div>
              </div>

              <div className="pt-3 flex items-center justify-end gap-3">
                <button
                  type="button"
                  onClick={closeFormModal}
                  className="px-4 py-2 rounded-lg bg-slate-200 text-slate-700 font-semibold hover:bg-slate-300"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  disabled={submitting}
                  className="px-4 py-2 rounded-lg bg-indigo-600 text-white font-semibold hover:bg-indigo-700 disabled:opacity-60"
                >
                  {submitting ? "Saving..." : "Save Ticket"}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default TicketAdmin;
