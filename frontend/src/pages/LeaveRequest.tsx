import { useEffect, useState } from "react";
import Navbar from "../components/Navbar";
import {
  fetchLeaves,
  createLeave,
  updateLeave,
  approveLeave,
  rejectLeave,
  deleteLeave,
  getLeaveTypes,
  type LeaveResponse,
  type LeaveRequest,
} from "../api/leaveService";
import { fetchEmployeeById } from "../api/employeeService";
import { getRole } from "../api/auth";

const getEidFromToken = (): string | null => {
  const token = localStorage.getItem("token");
  if (!token) return null;

  try {
    const payloadPart = token.split(".")[1];
    if (!payloadPart) return null;

    const base64 = payloadPart.replace(/-/g, "+").replace(/_/g, "/");
    const padded = base64.padEnd(Math.ceil(base64.length / 4) * 4, "=");
    const decoded = atob(padded);
    const parsed = JSON.parse(decoded) as { sub?: string };
    return parsed.sub ?? null;
  } catch {
    return null;
  }
};

const LeaveRequests = () => {
  const [leaves, setLeaves] = useState<LeaveResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [userRole, setUserRole] = useState<string | null>(null);
  const [currentUserId, setCurrentUserId] = useState<number | null>(null);
  const [leaveTypes, setLeaveTypes] = useState<
    Array<{ value: string; label: string }>
  >([]);
  const [showFormModal, setShowFormModal] = useState(false);
  const [formMode, setFormMode] = useState<"create" | "edit">("create");
  const [selectedLeave, setSelectedLeave] = useState<LeaveResponse | null>(
    null,
  );
  const [submitting, setSubmitting] = useState(false);
  const [filterStatus, setFilterStatus] = useState("all");
  const [searchTerm, setSearchTerm] = useState("");

  const [formData, setFormData] = useState<LeaveRequest>({
    userId: 0,
    startDate: "",
    endDate: "",
    type: "ANNUAL",
    subject: "",
  });

  const isAdmin = userRole?.toUpperCase() === "ADMIN";

  useEffect(() => {
    const loadData = async () => {
      setLoading(true);
      setError(null);

      try {
        const role = getRole();
        setUserRole(role);

        const eid = getEidFromToken();

        if (eid) {
          const currentUser = await fetchEmployeeById(eid);
          if (currentUser.id) {
            setCurrentUserId(currentUser.id);
          }
        }

        // Load leaves
        const leavesData = await fetchLeaves();
        setLeaves(leavesData);

        // Load leave types
        setLeaveTypes(getLeaveTypes());
      } catch (err) {
        console.error("Error loading data:", err);
        setError("Failed to load data. Please try again.");
      } finally {
        setLoading(false);
      }
    };

    loadData();
  }, []);

  const openCreateModal = () => {
    setFormMode("create");
    setSelectedLeave(null);
    // Prevent non-admin users from creating a second leave if they already have one
    if (!isAdmin && currentUserId) {
      const hasLeave = leaves.some((l) => l.user?.id === currentUserId);
      if (hasLeave) {
        setError(
          "You already have a leave request. Only one leave is allowed per user.",
        );
        return;
      }
    }

    setFormData({
      userId: isAdmin ? 0 : currentUserId || 0,
      startDate: "",
      endDate: "",
      type: "ANNUAL",
      subject: "",
    });
    setShowFormModal(true);
  };

  const openEditModal = (leave: LeaveResponse) => {
    setFormMode("edit");
    setSelectedLeave(leave);
    const startDateStr =
      typeof leave.startDate === "string"
        ? leave.startDate.split("T")[0]
        : new Date(leave.startDate).toISOString().split("T")[0];
    const endDateStr =
      typeof leave.endDate === "string"
        ? leave.endDate.split("T")[0]
        : new Date(leave.endDate).toISOString().split("T")[0];
    setFormData({
      userId: leave.user?.id || 0,
      startDate: startDateStr,
      endDate: endDateStr,
      type: leave.type,
      subject: leave.subject,
    });
    setShowFormModal(true);
  };

  const closeModal = () => {
    setShowFormModal(false);
    setSelectedLeave(null);
    setFormData({
      userId: isAdmin ? 0 : currentUserId || 0,
      startDate: "",
      endDate: "",
      type: "ANNUAL",
      subject: "",
    });
  };

  const handleFormChange = (
    e: React.ChangeEvent<
      HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement
    >,
  ) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: name === "userId" ? (value ? parseInt(value) : 0) : value,
    }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setSubmitting(true);
    setError(null);

    try {
      if (formMode === "create") {
        if (!formData.userId || !formData.startDate || !formData.endDate) {
          setError("Please fill in all required fields");
          setSubmitting(false);
          return;
        }

        const todayStr = new Date().toISOString().split("T")[0];
        if (formData.startDate < todayStr) {
          setError("Start date cannot be in the past");
          setSubmitting(false);
          return;
        }
        if (formData.endDate < formData.startDate) {
          setError("End date cannot be before start date");
          setSubmitting(false);
          return;
        }

        const newLeave = await createLeave(formData);
        setLeaves((prev) => [...prev, newLeave]);
      } else if (selectedLeave) {
        const updatePayload: Partial<LeaveRequest> = {
          startDate: formData.startDate,
          endDate: formData.endDate,
          type: formData.type,
          subject: formData.subject,
        };
        const updatedLeave = await updateLeave(selectedLeave.id, updatePayload);
        setLeaves((prev) =>
          prev.map((leave) =>
            leave.id === selectedLeave.id ? updatedLeave : leave,
          ),
        );
      }

      closeModal();
    } catch (err) {
      console.error("Error submitting form:", err);
      setError("Failed to submit leave request. Please try again.");
    } finally {
      setSubmitting(false);
    }
  };

  const handleApprove = async (leaveId: number) => {
    if (!currentUserId) return;

    try {
      setSubmitting(true);
      const updatedLeave = await approveLeave(leaveId, currentUserId);
      setLeaves((prev) =>
        prev.map((leave) => (leave.id === leaveId ? updatedLeave : leave)),
      );
    } catch (err) {
      console.error("Error approving leave:", err);
      setError("Failed to approve leave. Please try again.");
    } finally {
      setSubmitting(false);
    }
  };

  const handleReject = async (leaveId: number) => {
    if (!currentUserId) return;

    try {
      setSubmitting(true);
      const updatedLeave = await rejectLeave(leaveId, currentUserId);
      setLeaves((prev) =>
        prev.map((leave) => (leave.id === leaveId ? updatedLeave : leave)),
      );
    } catch (err) {
      console.error("Error rejecting leave:", err);
      setError("Failed to reject leave. Please try again.");
    } finally {
      setSubmitting(false);
    }
  };

  const handleDelete = async (leaveId: number) => {
    try {
      setSubmitting(true);
      await deleteLeave(leaveId);
      setLeaves((prev) => prev.filter((leave) => leave.id !== leaveId));
    } catch (err) {
      console.error("Error deleting leave:", err);
      setError("Failed to delete leave. Please try again.");
    } finally {
      setSubmitting(false);
    }
  };

  // Filter leaves based on role and search
  const filteredLeaves = leaves.filter((leave) => {
    const matchesRole = isAdmin || leave.user?.id === currentUserId;
    const matchesStatus =
      filterStatus === "all" || leave.status === filterStatus;
    const matchesSearch =
      !searchTerm ||
      leave.user?.firstName.toLowerCase().includes(searchTerm.toLowerCase()) ||
      leave.user?.lastName.toLowerCase().includes(searchTerm.toLowerCase()) ||
      leave.user?.email.toLowerCase().includes(searchTerm.toLowerCase());

    return matchesRole && matchesStatus && matchesSearch;
  });

  if (loading) {
    return (
      <div className="min-h-screen bg-slate-50">
        <Navbar
          userRole={userRole}
          title="Leave Requests"
          subtitle="Request and track your leave"
        />
        <main className="mx-auto max-w-7xl px-4 py-8 sm:px-6 lg:px-8">
          <p className="text-center text-slate-600">Loading...</p>
        </main>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-linear-to-br from-slate-900 via-slate-800 to-slate-900">
      <Navbar
        userRole={userRole}
        title={isAdmin ? "Leave Request Management" : "My Leave Requests"}
        subtitle={
          isAdmin
            ? "Manage and approve employee leave requests"
            : "Request and track your leave"
        }
      />

      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
        {/* Error Message */}
        {error && (
          <div className="mb-6 rounded-xl border-2 border-amber-300 bg-amber-50 p-4 text-amber-900 shadow-sm">
            <p className="font-semibold">Notice</p>
            <p className="text-sm text-amber-800">{error}</p>
          </div>
        )}

        {/* Stats for Admins */}
        {isAdmin && (
          <div className="grid grid-cols-1 gap-4 md:grid-cols-3 mb-8">
            <div className="rounded-xl bg-white p-5 shadow-lg ring-1 ring-slate-200">
              <p className="text-sm font-semibold uppercase tracking-widest text-slate-500">
                Total Requests
              </p>
              <p className="mt-2 text-3xl font-black text-slate-900">
                {leaves.length}
              </p>
            </div>
            <div className="rounded-xl bg-white p-5 shadow-lg ring-1 ring-slate-200">
              <p className="text-sm font-semibold uppercase tracking-widest text-slate-500">
                Pending
              </p>
              <p className="mt-2 text-3xl font-black text-amber-600">
                {leaves.filter((l) => l.status === "PENDING").length}
              </p>
            </div>
            <div className="rounded-xl bg-white p-5 shadow-lg ring-1 ring-slate-200">
              <p className="text-sm font-semibold uppercase tracking-widest text-slate-500">
                Approved
              </p>
              <p className="mt-2 text-3xl font-black text-emerald-600">
                {leaves.filter((l) => l.status === "APPROVED").length}
              </p>
            </div>
          </div>
        )}

        {/* New Leave Request Button */}
        <div className="mb-6">
          <button
            onClick={openCreateModal}
            className="rounded-lg bg-indigo-600 px-6 py-2 font-semibold text-white transition-colors hover:bg-indigo-700"
          >
            + New Leave Request
          </button>
        </div>

        {/* Filters */}
        <div className="mb-6 rounded-xl bg-white p-4 shadow-lg ring-1 ring-slate-200">
          <div className="grid grid-cols-1 gap-4 md:grid-cols-2">
            <input
              type="text"
              placeholder={
                isAdmin ? "Search by employee name or email..." : "Search..."
              }
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="rounded-lg border border-slate-300 px-4 py-2 text-slate-900 placeholder-slate-400 focus:border-indigo-500 focus:outline-none"
            />
            <select
              value={filterStatus}
              onChange={(e) => setFilterStatus(e.target.value)}
              className="rounded-lg border border-slate-300 px-4 py-2 text-slate-900 focus:border-indigo-500 focus:outline-none"
            >
              <option value="all">All Status</option>
              <option value="PENDING">Pending</option>
              <option value="APPROVED">Approved</option>
              <option value="REJECTED">Rejected</option>
            </select>
          </div>
        </div>

        {/* Leaves Table */}
        {filteredLeaves.length === 0 ? (
          <div className="rounded-xl bg-white p-12 text-center shadow-lg ring-1 ring-slate-200">
            <p className="text-slate-500">No leave requests found</p>
          </div>
        ) : (
          <div className="overflow-x-auto rounded-xl bg-white shadow-lg ring-1 ring-slate-200">
            <table className="w-full">
              <thead className="border-b border-slate-200 bg-slate-100">
                <tr>
                  <th className="px-6 py-3 text-left text-xs font-bold uppercase tracking-wider text-slate-900">
                    Start Date
                  </th>
                  {isAdmin && (
                    <th className="px-6 py-3 text-left text-xs font-bold uppercase tracking-wider text-slate-900">
                      Requester EID
                    </th>
                  )}
                  <th className="px-6 py-3 text-left text-xs font-bold uppercase tracking-wider text-slate-900">
                    End Date
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-bold uppercase tracking-wider text-slate-900">
                    Type
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-bold uppercase tracking-wider text-slate-900">
                    Status
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-bold uppercase tracking-wider text-slate-900">
                    Actions
                  </th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-200">
                {filteredLeaves.map((leave) => (
                  <tr
                    key={leave.id}
                    className="transition-colors hover:bg-slate-50"
                  >
                    <td className="px-6 py-4 text-sm text-slate-900">
                      {new Date(leave.startDate).toLocaleDateString()}
                    </td>
                    {isAdmin && (
                      <td className="px-6 py-4 text-sm font-semibold text-indigo-600">
                        {leave.user?.EID || "N/A"}
                      </td>
                    )}
                    <td className="px-6 py-4 text-sm text-slate-900">
                      {new Date(leave.endDate).toLocaleDateString()}
                    </td>
                    <td className="px-6 py-4 text-sm">
                      <span className="inline-block rounded-full bg-blue-100 px-3 py-1 text-xs font-semibold text-blue-800">
                        {leave.type}
                      </span>
                    </td>
                    <td className="px-6 py-4 text-sm">
                      <span
                        className={`inline-block rounded-full px-3 py-1 text-xs font-semibold ${
                          leave.status === "APPROVED"
                            ? "bg-emerald-100 text-emerald-800"
                            : leave.status === "REJECTED"
                              ? "bg-rose-100 text-rose-800"
                              : "bg-amber-100 text-amber-800"
                        }`}
                      >
                        {leave.status}
                      </span>
                    </td>
                    <td className="px-6 py-4 text-sm">
                      <div className="flex flex-wrap gap-2">
                        {isAdmin && leave.status === "PENDING" && (
                          <>
                            <button
                              onClick={() => handleApprove(leave.id)}
                              disabled={submitting}
                              className="rounded bg-emerald-600 px-3 py-1 text-xs font-semibold text-white transition-colors hover:bg-emerald-700 disabled:opacity-50"
                            >
                              Approve
                            </button>
                            <button
                              onClick={() => handleReject(leave.id)}
                              disabled={submitting}
                              className="rounded bg-rose-600 px-3 py-1 text-xs font-semibold text-white transition-colors hover:bg-rose-700 disabled:opacity-50"
                            >
                              Reject
                            </button>
                          </>
                        )}
                        {(!isAdmin || leave.status === "PENDING") && (
                          <button
                            onClick={() => openEditModal(leave)}
                            className="rounded bg-indigo-600 px-3 py-1 text-xs font-semibold text-white transition-colors hover:bg-indigo-700"
                          >
                            Edit
                          </button>
                        )}
                        <button
                          onClick={() => handleDelete(leave.id)}
                          className="rounded bg-slate-700 px-3 py-1 text-xs font-semibold text-white transition-colors hover:bg-slate-800"
                        >
                          Delete
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}

        {/* Create/Edit Modal */}
        {showFormModal && (
          <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 px-4">
            <div className="w-full max-w-md rounded-2xl bg-white shadow-2xl ring-1 ring-slate-200">
              <div className="p-6">
                <h3 className="mb-4 text-lg font-bold text-slate-900">
                  {formMode === "create"
                    ? "Request Leave"
                    : "Edit Leave Request"}
                </h3>

                <form onSubmit={handleSubmit} className="space-y-4">
                  <div>
                    <label className="block text-sm font-semibold text-slate-700 mb-1">
                      Start Date *
                    </label>
                    <input
                      type="date"
                      name="startDate"
                      value={formData.startDate}
                      onChange={handleFormChange}
                      min={new Date().toISOString().split("T")[0]}
                      className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:outline-none focus:border-indigo-500"
                      required
                    />
                  </div>

                  <div>
                    <label className="block text-sm font-semibold text-slate-700 mb-1">
                      End Date *
                    </label>
                    <input
                      type="date"
                      name="endDate"
                      value={formData.endDate}
                      onChange={handleFormChange}
                      min={
                        formData.startDate ||
                        new Date().toISOString().split("T")[0]
                      }
                      className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:outline-none focus:border-indigo-500"
                      required
                    />
                  </div>

                  <div>
                    <label className="block text-sm font-semibold text-slate-700 mb-1">
                      Leave Type
                    </label>
                    <select
                      name="type"
                      value={formData.type}
                      onChange={handleFormChange}
                      className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:outline-none focus:border-indigo-500"
                    >
                      {leaveTypes.map((type) => (
                        <option key={type.value} value={type.value}>
                          {type.label}
                        </option>
                      ))}
                    </select>
                  </div>

                  <div>
                    <label className="block text-sm font-semibold text-slate-700 mb-1">
                      Reason/Subject
                    </label>
                    <textarea
                      name="subject"
                      value={formData.subject || ""}
                      onChange={handleFormChange}
                      rows={3}
                      className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:outline-none focus:border-indigo-500"
                      placeholder="Leave reason"
                    />
                  </div>

                  <div className="flex gap-3 pt-4">
                    <button
                      type="submit"
                      disabled={submitting}
                      className="flex-1 bg-indigo-600 hover:bg-indigo-700 text-white font-bold py-2 rounded-lg transition-colors disabled:opacity-50"
                    >
                      {submitting
                        ? "Submitting..."
                        : formMode === "create"
                          ? "Request"
                          : "Update"}
                    </button>
                    <button
                      type="button"
                      onClick={closeModal}
                      disabled={submitting}
                      className="flex-1 bg-slate-300 hover:bg-slate-400 text-slate-900 font-bold py-2 rounded-lg transition-colors disabled:opacity-50"
                    >
                      Cancel
                    </button>
                  </div>
                </form>
              </div>
            </div>
          </div>
        )}
      </main>
    </div>
  );
};

export default LeaveRequests;
