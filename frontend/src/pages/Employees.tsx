import { useEffect, useState } from "react";
import Navbar from "../components/Navbar";
import {
  fetchEmployees,
  createEmployee,
  updateEmployee,
  deleteEmployee,
  fetchUserStatuses,
  fetchRoles,
  type EmployeeResponse,
  type EmployeeRequest,
  promoteEmployeeToTechnician,
} from "../api/employeeService";
import {
  fetchDepartments,
  type DepartmentResponse,
} from "../api/departmentService";
import { createTechnician, updateTechnician } from "../api/technicianService";
import { TEAM_GROUP_OPTIONS, fetchTeamGroups } from "../api/teamGroupService";
import { getRole } from "../api/auth";

const Employees = () => {
  const [employees, setEmployees] = useState<EmployeeResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [userRole, setUserRole] = useState<string | null>(null);
  const [statusOptions, setStatusOptions] = useState<
    Array<{ value: string; label: string }>
  >([]);
  const [roleOptions, setRoleOptions] = useState<
    Array<{ value: string; label: string }>
  >([]);
  const [departments, setDepartments] = useState<DepartmentResponse[]>([]);
  const [teamGroups, setTeamGroups] = useState<string[]>(TEAM_GROUP_OPTIONS);
  const [sortBy, setSortBy] = useState<keyof EmployeeResponse>("firstName");
  const [sortOrder, setSortOrder] = useState<"asc" | "desc">("asc");
  const [searchTerm, setSearchTerm] = useState("");
  const [filterStatus, setFilterStatus] = useState<string>("all");
  const [showModal, setShowModal] = useState(false);
  const [modalMode, setModalMode] = useState<"create" | "edit">("create");
  const [selectedEmployee, setSelectedEmployee] =
    useState<EmployeeResponse | null>(null);
  const [formData, setFormData] = useState<EmployeeRequest>({
    firstName: "",
    lastName: "",
    email: "",
    phone: "",
    password: "",
    departmentId: undefined,
    status: "ACTIVE",
    role: "",
    teamGroup: "",
  });
  const [editFormData, setEditFormData] = useState<{
    employeeId: string;
    firstName: string;
    lastName: string;
    email: string;
    phone: string;
    password: string;
    departmentId?: number;
    status: string;
    EID?: string;
    role?: string;
    teamGroup?: string;
    createdAt?: string;
    updatedAt?: string;
  }>({
    employeeId: "",
    firstName: "",
    lastName: "",
    email: "",
    phone: "",
    password: "",
    departmentId: undefined,
    status: "ACTIVE",
  });
  const [deleteConfirm, setDeleteConfirm] = useState<string | null>(null);

  const getToken = () => localStorage.getItem("token");

  const getEmployeeDepartmentName = (employee: EmployeeResponse) => {
    const flatDepartmentName = (
      employee as EmployeeResponse & { departement?: string }
    ).departement;

    return employee.department?.name || flatDepartmentName || "—";
  };

  const getEmployeeDepartmentId = (employee: EmployeeResponse) => {
    if (employee.departmentId) {
      return employee.departmentId;
    }

    const departmentName =
      employee.department?.name ||
      (employee as EmployeeResponse & { departement?: string }).departement;
    if (!departmentName) {
      return undefined;
    }

    return departments.find((dept) => dept.name === departmentName)
      ?.departmentId;
  };

  useEffect(() => {
    const loadData = async () => {
      setLoading(true);
      setError(null);
      try {
        const role = getRole();
        const token = getToken();

        if (!token || !role) {
          setError("Please log in to view employees.");
          setLoading(false);
          return;
        }

        setUserRole(role);

        if (!role.toLowerCase().includes("admin")) {
          setError(
            "You do not have permission to view the employees list. Only administrators can access this page.",
          );
          setLoading(false);
          return;
        }

        // Load employees, statuses, and roles in parallel
        const [employeesData, statuses, roles, departmentsData] =
          await Promise.all([
            fetchEmployees(),
            fetchUserStatuses(),
            fetchRoles(),
            fetchDepartments(),
          ]);

        const teamGroupsData = await fetchTeamGroups();

        setEmployees(employeesData);
        setStatusOptions(statuses);
        setRoleOptions(roles);
        setDepartments(departmentsData);
        setTeamGroups(
          teamGroupsData.length > 0
            ? teamGroupsData.map((group) => group.name)
            : TEAM_GROUP_OPTIONS,
        );
      } catch (err) {
        console.error("Error loading employees:", err);
        setError("Failed to load employees. Please try again.");
        setEmployees([]);
      } finally {
        setLoading(false);
      }
    };

    loadData();
  }, []);

  const filteredEmployees = employees
    .filter((emp) => {
      const matchesSearch =
        emp.firstName.toLowerCase().includes(searchTerm.toLowerCase()) ||
        emp.lastName.toLowerCase().includes(searchTerm.toLowerCase()) ||
        emp.email.toLowerCase().includes(searchTerm.toLowerCase());

      const matchesStatus =
        filterStatus === "all" || emp.status === filterStatus;

      return matchesSearch && matchesStatus;
    })
    .sort((a, b) => {
      const aValue = a[sortBy] || "";
      const bValue = b[sortBy] || "";

      let comparison = 0;
      if (typeof aValue === "string" && typeof bValue === "string") {
        comparison = aValue.localeCompare(bValue);
      } else if (typeof aValue === "number" && typeof bValue === "number") {
        comparison = aValue - bValue;
      }

      return sortOrder === "asc" ? comparison : -comparison;
    });

  const handleSort = (column: keyof EmployeeResponse) => {
    if (sortBy === column) {
      setSortOrder(sortOrder === "asc" ? "desc" : "asc");
    } else {
      setSortBy(column);
      setSortOrder("asc");
    }
  };

  const openCreateModal = () => {
    setModalMode("create");
    setSelectedEmployee(null);
    setFormData({
      firstName: "",
      lastName: "",
      email: "",
      phone: "",
      password: "",
      departmentId: undefined,
      status: "ACTIVE",
      role: "",
      teamGroup: "",
    });
    setShowModal(true);
  };

  const openEditModal = (employee: EmployeeResponse) => {
    setModalMode("edit");
    setSelectedEmployee(employee);
    setEditFormData({
      employeeId: employee.employeeId || "",
      firstName: employee.firstName,
      lastName: employee.lastName,
      email: employee.email,
      phone: employee.phone || "",
      password: "",
      departmentId: getEmployeeDepartmentId(employee),
      status: employee.status || "ACTIVE",
      EID: employee.EID || "",
      role: employee.role || "Employee",
      teamGroup: employee.teamGroup || "",
      createdAt: employee.createdAt || "",
      updatedAt: employee.updatedAt || "",
    });
    setShowModal(true);
  };

  const closeModal = () => {
    setShowModal(false);
    setSelectedEmployee(null);
    setFormData({
      firstName: "",
      lastName: "",
      email: "",
      phone: "",
      password: "",
      departmentId: undefined,
      status: "ACTIVE",
      role: "",
      teamGroup: "",
    });
    setEditFormData({
      employeeId: "",
      firstName: "",
      lastName: "",
      email: "",
      phone: "",
      password: "",
      departmentId: undefined,
      status: "ACTIVE",
      teamGroup: "",
    });
  };

  const handleFormChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>,
  ) => {
    const { name, value } = e.target;

    if (modalMode === "edit") {
      setEditFormData((prev) => ({
        ...prev,
        [name]:
          name === "departmentId"
            ? value
              ? parseInt(value)
              : undefined
            : value,
        ...(name === "role" && value.toUpperCase() !== "TECHNICIAN"
          ? { teamGroup: "" }
          : {}),
      }));
    } else {
      setFormData((prev) => ({
        ...prev,
        [name]:
          name === "departmentId"
            ? value
              ? parseInt(value)
              : undefined
            : value,
        ...(name === "role" && value.toUpperCase() !== "TECHNICIAN"
          ? { teamGroup: "" }
          : {}),
      }));
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);

    try {
      const activeRole = (
        modalMode === "create" ? formData.role : editFormData.role
      )?.toUpperCase();

      if (modalMode === "create") {
        if (activeRole === "TECHNICIAN") {
          if (!formData.teamGroup) {
            setError("Please select a team group for the technician.");
            return;
          }

          if (!formData.password) {
            setError("Password is required for a new technician.");
            return;
          }

          const newTechnician = await createTechnician({
            firstName: formData.firstName,
            lastName: formData.lastName,
            email: formData.email,
            phone: formData.phone || "",
            password: formData.password,
            status: formData.status,
            teamGroup: formData.teamGroup,
          });

          setEmployees((prev) => [...prev, newTechnician as EmployeeResponse]);
        } else {
          const newEmployee = await createEmployee(formData);
          setEmployees((prev) => [...prev, newEmployee]);
        }
      } else if (selectedEmployee) {
        if (
          activeRole === "TECHNICIAN" ||
          selectedEmployee.role === "TECHNICIAN"
        ) {
          if (!editFormData.teamGroup) {
            setError("Please select a team group for the technician.");
            return;
          }

          if (selectedEmployee.role === "TECHNICIAN") {
            const updatedTechnician = await updateTechnician(
              selectedEmployee.EID || "",
              {
                firstName: editFormData.firstName,
                lastName: editFormData.lastName,
                email: editFormData.email,
                phone: editFormData.phone,
                password: editFormData.password || undefined,
                status: editFormData.status,
                teamGroup: editFormData.teamGroup,
              },
            );

            setEmployees((prev) =>
              prev.map((emp) =>
                (emp.employeeId || emp.id) ===
                (selectedEmployee.employeeId || selectedEmployee.id)
                  ? (updatedTechnician as EmployeeResponse)
                  : emp,
              ),
            );
          } else {
            const updatedEmployee = await updateEmployee(
              selectedEmployee.EID || "",
              {
                firstName: editFormData.firstName,
                lastName: editFormData.lastName,
                email: editFormData.email,
                phone: editFormData.phone,
                departmentId: editFormData.departmentId,
                status: editFormData.status,
                role: editFormData.role,
              },
            );

            const promotedTechnician = await promoteEmployeeToTechnician(
              selectedEmployee.id || 0,
              editFormData.teamGroup,
            );

            setEmployees((prev) =>
              prev.map((emp) =>
                (emp.employeeId || emp.id) ===
                (selectedEmployee.employeeId || selectedEmployee.id)
                  ? {
                      ...updatedEmployee,
                      ...promotedTechnician,
                    }
                  : emp,
              ),
            );
          }
        } else {
          // When editing, only send modifiable fields (not eid, createdAt, updatedAt)
          const updatePayload: Partial<EmployeeRequest> = {
            firstName: editFormData.firstName,
            lastName: editFormData.lastName,
            email: editFormData.email,
            phone: editFormData.phone,
            departmentId: editFormData.departmentId,
            status: editFormData.status,
            role: editFormData.role,
          };
          const updatedEmployee = await updateEmployee(
            selectedEmployee.EID || "",
            updatePayload,
          );
          setEmployees((prev) =>
            prev.map((emp) =>
              (emp.employeeId || emp.id) ===
              (selectedEmployee.employeeId || selectedEmployee.id)
                ? updatedEmployee
                : emp,
            ),
          );
        }
      }
      closeModal();
    } catch (err) {
      console.error("Error submitting form:", err);
      setError(
        modalMode === "create"
          ? "Failed to create employee. Please try again."
          : "Failed to update employee. Please try again.",
      );
    }
  };

  const handleDelete = async (eid: string) => {
    try {
      await deleteEmployee(eid);
      setEmployees((prev) => prev.filter((emp) => emp.EID !== eid));
      setDeleteConfirm(null);
    } catch (err) {
      console.error("Error deleting employee:", err);
      setError("Failed to delete employee. Please try again.");
    }
  };

  const getSortIcon = (column: keyof EmployeeResponse) => {
    if (sortBy !== column) {
      return (
        <svg
          className="w-4 h-4 opacity-40"
          fill="none"
          stroke="currentColor"
          viewBox="0 0 24 24"
        >
          <path
            strokeLinecap="round"
            strokeLinejoin="round"
            strokeWidth={2}
            d="M7 16V4m0 0L3 8m4-4l4 4m6 0v12m0 0l4-4m-4 4l-4-4"
          />
        </svg>
      );
    }

    return sortOrder === "asc" ? (
      <svg className="w-4 h-4" fill="currentColor" viewBox="0 0 24 24">
        <path d="M7 14l5-5 5 5z" />
      </svg>
    ) : (
      <svg className="w-4 h-4" fill="currentColor" viewBox="0 0 24 24">
        <path d="M7 10l5 5 5-5z" />
      </svg>
    );
  };

  return (
    <div className="min-h-screen bg-linear-to-br from-slate-900 via-slate-800 to-slate-900">
      <Navbar
        userRole={userRole}
        title="Employees Management"
        subtitle="Manage and organize your employee database"
      />

      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
        {/* Error Alert */}
        {error && (
          <div className="mb-6 p-5 bg-yellow-50 border-2 border-yellow-400 rounded-lg flex items-start gap-4">
            <svg
              xmlns="http://www.w3.org/2000/svg"
              fill="none"
              viewBox="0 0 24 24"
              strokeWidth={1.5}
              stroke="currentColor"
              className="w-6 h-6 text-yellow-600 shrink-0 mt-0.5"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                d="M12 9v3.75m9-.75a9 9 0 1 1-18 0 9 9 0 0 1 18 0Zm-9 3.75h.008v.008H12v-.008Z"
              />
            </svg>
            <div>
              <p className="text-sm font-semibold text-yellow-900 mb-1">
                Notice
              </p>
              <p className="text-sm text-yellow-800">{error}</p>
            </div>
          </div>
        )}

        {/* Loading State */}
        {loading ? (
          <div className="flex items-center justify-center py-12">
            <div className="text-center">
              <div className="inline-block animate-spin rounded-full h-12 w-12 border-b-2 border-indigo-600 mb-4"></div>
              <p className="text-white">Loading employees...</p>
            </div>
          </div>
        ) : (
          <>
            {/* Header with Stats and Button */}
            <div className="mb-8">
              <div className="flex items-center justify-between mb-6">
                <div>
                  <h2 className="text-2xl font-bold text-white flex items-center gap-2">
                    <svg
                      xmlns="http://www.w3.org/2000/svg"
                      fill="none"
                      viewBox="0 0 24 24"
                      strokeWidth={2}
                      stroke="currentColor"
                      className="w-8 h-8"
                    >
                      <path
                        strokeLinecap="round"
                        strokeLinejoin="round"
                        d="M15 19.128a9.38 9.38 0 0 0 2.625.372 9.337 9.337 0 0 0 4.121-.952 4.125 4.125 0 0 0-7.533-2.493M15 19.128v-.003c0-1.657-1.338-3-2.991-3H5.009c-1.653 0-2.991 1.343-2.991 3v.006"
                      />
                    </svg>
                    Employee List
                  </h2>
                  <p className="text-gray-400 mt-1">
                    Total Employees:{" "}
                    <span className="font-bold text-indigo-400">
                      {filteredEmployees.length}
                    </span>
                  </p>
                </div>
                <button
                  onClick={openCreateModal}
                  className="bg-indigo-600 hover:bg-indigo-700 text-white font-bold py-3 px-6 rounded-lg transition-all duration-200 flex items-center gap-2 shadow-lg hover:shadow-xl"
                >
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    fill="none"
                    viewBox="0 0 24 24"
                    strokeWidth={2}
                    stroke="currentColor"
                    className="w-5 h-5"
                  >
                    <path
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      d="M12 4.5v15m7.5-7.5h-15"
                    />
                  </svg>
                  Add Employee
                </button>
              </div>

              {/* Search and Filter */}
              <div className="flex flex-col md:flex-row gap-4">
                <div className="flex-1">
                  <input
                    type="text"
                    placeholder="Search by name or email..."
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                    className="w-full px-4 py-3 bg-slate-700 text-white placeholder-gray-400 border border-slate-600 rounded-lg focus:outline-none focus:border-indigo-500 "
                  />
                </div>
                <select
                  value={filterStatus}
                  onChange={(e) => setFilterStatus(e.target.value)}
                  className="px-4 py-3 bg-slate-700 text-white border border-slate-600 rounded-lg focus:outline-none focus:border-indigo-500 "
                >
                  <option value="all">All Status</option>
                  {statusOptions.map((status) => (
                    <option key={status.value} value={status.value}>
                      {status.label}
                    </option>
                  ))}
                </select>
              </div>
            </div>

            {/* Table */}
            <div className="bg-white rounded-lg shadow-lg overflow-hidden">
              {filteredEmployees.length === 0 ? (
                <div className="text-center py-12">
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    fill="none"
                    viewBox="0 0 24 24"
                    strokeWidth={1.5}
                    stroke="currentColor"
                    className="w-12 h-12 text-gray-400 mx-auto mb-4"
                  >
                    <path
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      d="M15 19.128a9.38 9.38 0 0 0 2.625.372 9.337 9.337 0 0 0 4.121-.952 4.125 4.125 0 0 0-7.533-2.493M15 19.128v-.003c0-1.657-1.338-3-2.991-3H5.009c-1.653 0-2.991 1.343-2.991 3v.006"
                    />
                  </svg>
                  <p className="text-gray-500 text-lg font-medium">
                    No employees found
                  </p>
                  <p className="text-gray-400 text-sm mt-1">
                    {searchTerm || filterStatus !== "all"
                      ? "Try adjusting your search or filter criteria"
                      : "Start by adding your first employee"}
                  </p>
                </div>
              ) : (
                <div className="overflow-x-auto">
                  <table className="w-full">
                    <thead>
                      <tr className="bg-linear-to-r from-indigo-600 to-indigo-700 text-white">
                        <th className="px-6 py-4 text-left">
                          <button
                            onClick={() => handleSort("EID")}
                            className="flex items-center gap-2 font-semibold hover:opacity-80 transition-opacity"
                          >
                            EID
                            {getSortIcon("EID")}
                          </button>
                        </th>
                        <th className="px-6 py-4 text-left">
                          <button
                            onClick={() => handleSort("firstName")}
                            className="flex items-center gap-2 font-semibold hover:opacity-80 transition-opacity"
                          >
                            Name
                            {getSortIcon("firstName")}
                          </button>
                        </th>
                        <th className="px-6 py-4 text-left">
                          <button
                            onClick={() => handleSort("email")}
                            className="flex items-center gap-2 font-semibold hover:opacity-80 transition-opacity"
                          >
                            Email
                            {getSortIcon("email")}
                          </button>
                        </th>
                        <th className="px-6 py-4 text-left">
                          <button
                            onClick={() => handleSort("phone")}
                            className="flex items-center gap-2 font-semibold hover:opacity-80 transition-opacity"
                          >
                            Phone
                            {getSortIcon("phone")}
                          </button>
                        </th>
                        <th className="px-6 py-4 text-left">
                          <button
                            onClick={() => handleSort("status")}
                            className="flex items-center gap-2 font-semibold hover:opacity-80 transition-opacity"
                          >
                            Status
                            {getSortIcon("status")}
                          </button>
                        </th>
                        <th className="px-6 py-4 text-left">
                          <button
                            onClick={() => handleSort("departmentId")}
                            className="flex items-center gap-2 font-semibold hover:opacity-80 transition-opacity"
                          >
                            Department
                            {getSortIcon("departmentId")}
                          </button>
                        </th>
                        <th className="px-6 py-4 text-center font-semibold">
                          Actions
                        </th>
                      </tr>
                    </thead>
                    <tbody>
                      {filteredEmployees.map((employee, index) => (
                        <tr
                          key={employee.employeeId || employee.id || index}
                          className={`border-b transition-colors ${
                            index % 2 === 0 ? "bg-gray-50" : "bg-white"
                          } hover:bg-indigo-50`}
                        >
                          <td className="px-6 py-4 font-medium text-gray-900">
                            {employee.EID}
                          </td>
                          <td className="px-6 py-4 font-medium text-gray-900">
                            {employee.firstName} {employee.lastName}
                          </td>
                          <td className="px-6 py-4 text-gray-600">
                            {employee.email}
                          </td>
                          <td className="px-6 py-4 text-gray-600">
                            {employee.phone || "—"}
                          </td>
                          <td className="px-6 py-4">
                            <span
                              className={`inline-block px-3 py-1 rounded-full text-xs font-semibold ${
                                employee.status === "ACTIVE"
                                  ? "bg-green-100 text-green-800"
                                  : employee.status === "ON_LEAVE"
                                    ? "bg-blue-100 text-blue-800"
                                    : "bg-gray-100 text-gray-800"
                              }`}
                            >
                              {employee.status || "ACTIVE"}
                            </span>
                          </td>
                          <td className="px-6 py-4 text-gray-600 font-mono text-sm">
                            {getEmployeeDepartmentName(employee)}
                          </td>
                          <td className="px-6 py-4 text-center">
                            <div className="flex items-center justify-center gap-2">
                              <button
                                onClick={() => openEditModal(employee)}
                                className="p-2 bg-blue-100 text-blue-600 rounded-lg hover:bg-blue-200 transition-colors"
                                title="Edit employee"
                              >
                                <svg
                                  xmlns="http://www.w3.org/2000/svg"
                                  fill="none"
                                  viewBox="0 0 24 24"
                                  strokeWidth={2}
                                  stroke="currentColor"
                                  className="w-4 h-4"
                                >
                                  <path
                                    strokeLinecap="round"
                                    strokeLinejoin="round"
                                    d="M16.862 4.487l1.687-1.688a1.875 1.875 0 112.652 2.652L10.582 16.07a4.5 4.5 0 01-1.897 1.13L6 18l.8-2.685a4.5 4.5 0 011.13-1.897l8.932-8.931zm0 0L19.5 7.125M18 9.75a3.75 3.75 0 11-7.5 0 3.75 3.75 0 017.5 0z"
                                  />
                                </svg>
                              </button>
                              <button
                                onClick={() =>
                                  setDeleteConfirm(employee.EID || "")
                                }
                                className="p-2 bg-red-100 text-red-600 rounded-lg hover:bg-red-200 transition-colors"
                                title="Delete employee"
                              >
                                <svg
                                  xmlns="http://www.w3.org/2000/svg"
                                  fill="none"
                                  viewBox="0 0 24 24"
                                  strokeWidth={2}
                                  stroke="currentColor"
                                  className="w-4 h-4"
                                >
                                  <path
                                    strokeLinecap="round"
                                    strokeLinejoin="round"
                                    d="M14.74 9l-.346 9m-4.788 0L9.26 9m9.968-3.21c.342.052.682.107 1.022.166m-1.022-.165L18.16 2.991a1.5 1.5 0 00-1.06-.44h-12.2a1.5 1.5 0 00-1.061.44L2.05 5.624m15.9 7.228h2.123M7.728 13h.01M12 13h.01M15.272 13h.01M7.363 5.697a6 6 0 0111.279 0"
                                  />
                                </svg>
                              </button>
                            </div>
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              )}
            </div>
          </>
        )}
      </main>

      {/* Modal for Create/Edit */}
      {showModal && (
        <div className="fixed inset-0 bg-black/50 z-50 flex items-center justify-center p-4">
          <div className="bg-white rounded-lg shadow-xl w-full max-w-2xl max-h-96 overflow-y-auto">
            <div className="sticky top-0 bg-linear-to-r from-indigo-600 to-indigo-700 px-6 py-4 text-white flex items-center justify-between">
              <h3 className="text-xl font-bold">
                {modalMode === "create" ? "Add New Employee" : "Edit Employee"}
              </h3>
              <button
                onClick={closeModal}
                className="text-white hover:bg-white/20 rounded-lg p-1 transition-colors"
              >
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  fill="none"
                  viewBox="0 0 24 24"
                  strokeWidth={2}
                  stroke="currentColor"
                  className="w-6 h-6"
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    d="M6 18L18 6M6 6l12 12"
                  />
                </svg>
              </button>
            </div>

            <form onSubmit={handleSubmit} className="p-6 space-y-4">
              {/* Read-only fields in edit mode */}
              {modalMode === "edit" && (
                <>
                  <div className="grid grid-cols-1 md:grid-cols-2 gap-4 bg-gray-50 p-4 rounded-lg border border-gray-200">
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-2">
                        EID
                      </label>
                      <input
                        type="text"
                        value={editFormData.EID}
                        disabled
                        className="w-full px-4 py-2 bg-gray-100 border border-gray-300 text-gray-600 rounded-lg cursor-not-allowed"
                      />
                    </div>
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-2">
                        Created At
                      </label>
                      <input
                        type="text"
                        value={editFormData.createdAt}
                        disabled
                        className="w-full px-4 py-2 bg-gray-100 border border-gray-300 text-gray-600 rounded-lg cursor-not-allowed"
                      />
                    </div>
                    <div className="md:col-span-2">
                      <label className="block text-sm font-medium text-gray-700 mb-2">
                        Updated At
                      </label>
                      <input
                        type="text"
                        value={editFormData.updatedAt}
                        disabled
                        className="w-full px-4 py-2 bg-gray-100 border border-gray-300 text-gray-600 rounded-lg cursor-not-allowed"
                      />
                    </div>
                  </div>
                  <div className="border-t pt-4"></div>
                </>
              )}

              {/* Editable fields */}
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    First Name *
                  </label>
                  <input
                    type="text"
                    name="firstName"
                    value={
                      modalMode === "create"
                        ? formData.firstName
                        : editFormData.firstName
                    }
                    onChange={handleFormChange}
                    required
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:border-indigo-500 "
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Last Name *
                  </label>
                  <input
                    type="text"
                    name="lastName"
                    value={
                      modalMode === "create"
                        ? formData.lastName
                        : editFormData.lastName
                    }
                    onChange={handleFormChange}
                    required
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:border-indigo-500 "
                  />
                </div>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Email *
                </label>
                <input
                  type="email"
                  name="email"
                  value={
                    modalMode === "create" ? formData.email : editFormData.email
                  }
                  onChange={handleFormChange}
                  required
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:border-indigo-500 "
                />
              </div>
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Phone
                  </label>
                  <input
                    type="tel"
                    name="phone"
                    value={
                      modalMode === "create"
                        ? formData.phone
                        : editFormData.phone
                    }
                    onChange={handleFormChange}
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:border-indigo-500 "
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Status
                  </label>
                  <select
                    name="status"
                    value={
                      modalMode === "create"
                        ? formData.status
                        : editFormData.status
                    }
                    onChange={handleFormChange}
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:border-indigo-500 "
                  >
                    <option value="">Select a status</option>
                    {statusOptions.map((status) => (
                      <option key={status.value} value={status.value}>
                        {status.label}
                      </option>
                    ))}
                  </select>
                </div>
              </div>

              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Role
                  </label>
                  <select
                    name="role"
                    value={
                      modalMode === "create"
                        ? formData.role || ""
                        : editFormData.role || ""
                    }
                    onChange={handleFormChange}
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:border-indigo-500 "
                  >
                    <option value="">Select a role</option>
                    {roleOptions.map((role) => (
                      <option key={role.value} value={role.value}>
                        {role.label}
                      </option>
                    ))}
                  </select>
                </div>
              </div>

              {(modalMode === "create" ? formData.role : editFormData.role) ===
                "TECHNICIAN" && (
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      Team Group
                    </label>
                    <select
                      name="teamGroup"
                      value={
                        modalMode === "create"
                          ? formData.teamGroup || ""
                          : editFormData.teamGroup || ""
                      }
                      onChange={handleFormChange}
                      className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:border-indigo-500"
                    >
                      <option value="">Select a team group</option>
                      {teamGroups.map((group) => (
                        <option key={group} value={group}>
                          {group}
                        </option>
                      ))}
                    </select>
                  </div>
                </div>
              )}

              {(modalMode === "create" ? formData.role : editFormData.role) !==
                "TECHNICIAN" && (
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Department
                  </label>
                  <select
                    name="departmentId"
                    value={
                      modalMode === "create"
                        ? formData.departmentId || ""
                        : editFormData.departmentId || ""
                    }
                    onChange={handleFormChange}
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:border-indigo-500"
                  >
                    <option value="">Select a department</option>
                    {departments.map((dept) => (
                      <option
                        key={dept.departmentId}
                        value={dept.departmentId || ""}
                      >
                        {dept.name}
                      </option>
                    ))}
                  </select>
                </div>
              )}

              {modalMode === "create" && (
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Password *
                  </label>
                  <input
                    type="password"
                    name="password"
                    value={formData.password}
                    onChange={handleFormChange}
                    required={modalMode === "create"}
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:border-indigo-500 "
                  />
                </div>
              )}

              <div className="flex gap-3 justify-end pt-4">
                <button
                  type="button"
                  onClick={closeModal}
                  className="px-6 py-2 bg-gray-200 text-gray-800 rounded-lg font-medium hover:bg-gray-300 transition-colors"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-6 py-2 bg-indigo-600 text-white rounded-lg font-medium hover:bg-indigo-700 transition-colors"
                >
                  {modalMode === "create" ? "Create" : "Update"}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Delete Confirmation Modal */}
      {deleteConfirm !== null && (
        <div className="fixed inset-0 bg-black/50 z-50 flex items-center justify-center p-4">
          <div className="bg-white rounded-lg shadow-xl max-w-sm">
            <div className="bg-red-50 px-6 py-4 border-b border-red-200">
              <h3 className="text-lg font-bold text-red-900">
                Delete Employee?
              </h3>
            </div>
            <div className="px-6 py-4">
              <p className="text-gray-700">
                Are you sure you want to delete this employee? This action
                cannot be undone.
              </p>
            </div>
            <div className="bg-gray-50 px-6 py-4 border-t flex gap-3 justify-end">
              <button
                onClick={() => setDeleteConfirm(null)}
                className="px-4 py-2 bg-gray-200 text-gray-800 rounded-lg font-medium hover:bg-gray-300 transition-colors"
              >
                Cancel
              </button>
              <button
                onClick={() => handleDelete(deleteConfirm)}
                className="px-4 py-2 bg-red-600 text-white rounded-lg font-medium hover:bg-red-700 transition-colors"
              >
                Delete
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Footer */}
      <footer className="bg-gray-900 border-t border-gray-800 mt-12">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
          <p className="text-center text-gray-400 text-sm">
            © 2024 Marjane Employee Management System. All rights reserved.
          </p>
        </div>
      </footer>
    </div>
  );
};

export default Employees;
