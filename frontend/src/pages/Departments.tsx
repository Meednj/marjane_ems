import { useEffect, useState } from "react";
import Navbar from "../components/Navbar";
import { getRole } from "../api/auth";
import {
  createDepartment,
  updateDepartment,
  deleteDepartment,
  fetchDepartments,
  type DepartmentResponse,
  type DepartmentPayload,
} from "../api/departmentService";

const Departments = () => {
  const [departments, setDepartments] = useState<DepartmentResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [userRole, setUserRole] = useState<string | null>(null);
  const [sortBy, setSortBy] = useState<keyof DepartmentResponse>("name");
  const [sortOrder, setSortOrder] = useState<"asc" | "desc">("asc");
  const [searchTerm, setSearchTerm] = useState("");
  const [showModal, setShowModal] = useState(false);
  const [modalMode, setModalMode] = useState<"create" | "edit">("create");
  const [selectedDepartment, setSelectedDepartment] =
    useState<DepartmentResponse | null>(null);
  const [formData, setFormData] = useState<DepartmentPayload>({
    name: "",
    description: "",
  });
  const [editFormData, setEditFormData] = useState<
    DepartmentPayload & { departmentId?: number }
  >({
    name: "",
    description: "",
  });
  const [deleteConfirm, setDeleteConfirm] = useState<number | null>(null);

  useEffect(() => {
    const loadData = async () => {
      setLoading(true);
      setError(null);
      try {
        const role = getRole();
        setUserRole(role);

        if (!role || !role.toLowerCase().includes("admin")) {
          setError(
            "You do not have permission to view departments. Only administrators can access this page.",
          );
          setLoading(false);
          return;
        }

        const departmentsData = await fetchDepartments();
        setDepartments(departmentsData);
      } catch (err) {
        console.error("Error loading departments:", err);
        setError("Failed to load departments. Please try again.");
        setDepartments([]);
      } finally {
        setLoading(false);
      }
    };

    loadData();
  }, []);

  const filteredDepartments = departments
    .filter((dept) => {
      const matchesSearch =
        dept.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
        (dept.description &&
          dept.description.toLowerCase().includes(searchTerm.toLowerCase()));

      return matchesSearch;
    })
    .sort((a, b) => {
      const aValue = a[sortBy] || "";
      const bValue = b[sortBy] || "";

      let comparison = 0;
      if (typeof aValue === "string" && typeof bValue === "string") {
        comparison = aValue.localeCompare(bValue);
      }

      return sortOrder === "asc" ? comparison : -comparison;
    });

  const handleSort = (column: keyof DepartmentResponse) => {
    if (sortBy === column) {
      setSortOrder(sortOrder === "asc" ? "desc" : "asc");
    } else {
      setSortBy(column);
      setSortOrder("asc");
    }
  };

  const openCreateModal = () => {
    setModalMode("create");
    setSelectedDepartment(null);
    setFormData({
      name: "",
      description: "",
    });
    setShowModal(true);
  };

  const openEditModal = (department: DepartmentResponse) => {
    setModalMode("edit");
    setSelectedDepartment(department);
    setEditFormData({
      departmentId: department.departmentId,
      name: department.name,
      description: department.description,
    });
    setShowModal(true);
  };

  const closeModal = () => {
    setShowModal(false);
    setSelectedDepartment(null);
    setFormData({
      name: "",
      description: "",
    });
    setEditFormData({
      name: "",
      description: "",
    });
  };

  const handleFormChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>,
  ) => {
    const { name, value } = e.target;

    if (modalMode === "edit") {
      setEditFormData((prev) => ({
        ...prev,
        [name]: value,
      }));
    } else {
      setFormData((prev) => ({
        ...prev,
        [name]: value,
      }));
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);

    try {
      if (modalMode === "create") {
        const newDepartment = await createDepartment(formData);
        setDepartments((prev) => [...prev, newDepartment]);
      } else if (selectedDepartment && selectedDepartment.departmentId) {
        const updatePayload: Partial<DepartmentPayload> = {
          name: editFormData.name,
          description: editFormData.description,
        };
        const updatedDepartment = await updateDepartment(
          selectedDepartment.departmentId,
          updatePayload,
        );
        setDepartments((prev) =>
          prev.map((dept) =>
            dept.departmentId === selectedDepartment.departmentId
              ? updatedDepartment
              : dept,
          ),
        );
      }
      closeModal();
    } catch (err) {
      console.error("Error submitting form:", err);
      setError(
        modalMode === "create"
          ? "Failed to create department. Please try again."
          : "Failed to update department. Please try again.",
      );
    }
  };

  const handleDelete = async (id: number) => {
    try {
      await deleteDepartment(id);

      setDepartments((prev) => prev.filter((dept) => dept.departmentId !== id));

      setDeleteConfirm(null);
    } catch (err) {
      console.error("Error deleting department:", err);
      setError("Failed to delete department. Please try again.");
    }
  };

  const getSortIcon = (column: keyof DepartmentResponse) => {
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
    <div className="min-h-screen bg-gradient-to-br from-slate-900 via-slate-800 to-slate-900">
      <Navbar
        userRole={userRole}
        title="Departments Management"
        subtitle="Manage organizational departments"
      />

      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
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

        {loading ? (
          <div className="flex items-center justify-center py-12">
            <div className="text-center">
              <div className="inline-block animate-spin rounded-full h-12 w-12 border-b-2 border-indigo-600 mb-4"></div>
              <p className="text-white">Loading departments...</p>
            </div>
          </div>
        ) : (
          <>
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
                        d="M7 7h.01M7 3h5c.512 0 1.024.195 1.414.586l7 7a2 2 0 0 1 0 2.828l-7 7a2 2 0 0 1-2.828 0l-7-7A1.994 1.994 0 0 1 2 12V7a2 2 0 0 1 2-2z"
                      />
                    </svg>
                    Department List
                  </h2>
                  <p className="text-gray-400 mt-1">
                    Total Departments:{" "}
                    <span className="font-bold text-indigo-400">
                      {filteredDepartments.length}
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
                  New Department
                </button>
              </div>

              <div className="mb-6">
                <input
                  type="text"
                  placeholder="Search departments..."
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                  className="w-full px-4 py-2 bg-slate-700 text-white rounded-lg border border-slate-600 placeholder-slate-400 focus:border-indigo-500 focus:outline-none"
                />
              </div>
            </div>

            {filteredDepartments.length === 0 ? (
              <div className="text-center py-12">
                <p className="text-gray-400">No departments found</p>
              </div>
            ) : (
              <div className="bg-white rounded-lg shadow-lg overflow-x-auto">
                <table className="w-full">
                  <thead className="bg-slate-100 border-b border-slate-200">
                    <tr>
                      <th
                        className="px-6 py-3 text-left text-xs font-bold text-slate-900 uppercase tracking-wider cursor-pointer hover:bg-slate-200"
                        onClick={() => handleSort("name")}
                      >
                        <div className="flex items-center gap-2">
                          Name {getSortIcon("name")}
                        </div>
                      </th>
                      <th className="px-6 py-3 text-left text-xs font-bold text-slate-900 uppercase tracking-wider">
                        Description
                      </th>
                      <th className="px-6 py-3 text-left text-xs font-bold text-slate-900 uppercase tracking-wider">
                        Actions
                      </th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-slate-200">
                    {filteredDepartments.map((dept) => (
                      <tr
                        key={dept.departmentId}
                        className="hover:bg-slate-50 transition-colors"
                      >
                        <td className="px-6 py-4 text-sm font-semibold text-slate-900">
                          {dept.name}
                        </td>
                        <td className="px-6 py-4 text-sm text-slate-600">
                          {dept.description || "—"}
                        </td>
                        <td className="px-6 py-4 text-sm">
                          <div className="flex gap-2">
                            <button
                              onClick={() => openEditModal(dept)}
                              className="px-4 py-2 bg-indigo-500 hover:bg-indigo-600 text-white rounded text-xs font-semibold transition-colors"
                            >
                              Edit
                            </button>
                            <button
                              onClick={() =>
                                setDeleteConfirm(dept.departmentId!)
                              }
                              className="px-4 py-2 bg-red-500 hover:bg-red-600 text-white rounded text-xs font-semibold transition-colors"
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
          </>
        )}

        {/* Create/Edit Modal */}
        {showModal && (
          <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
            <div className="bg-white rounded-lg shadow-xl max-w-md w-full mx-4">
              <div className="p-6">
                <h3 className="text-lg font-bold text-slate-900 mb-4">
                  {modalMode === "create"
                    ? "Create Department"
                    : "Edit Department"}
                </h3>

                <form onSubmit={handleSubmit} className="space-y-4">
                  <div>
                    <label className="block text-sm font-semibold text-slate-900 mb-1">
                      Department Name *
                    </label>
                    <input
                      type="text"
                      name="name"
                      value={
                        modalMode === "create"
                          ? formData.name
                          : editFormData.name
                      }
                      onChange={handleFormChange}
                      placeholder="e.g., Sales, IT, HR"
                      className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:outline-none focus:border-indigo-500"
                      required
                    />
                  </div>

                  <div>
                    <label className="block text-sm font-semibold text-slate-900 mb-1">
                      Description
                    </label>
                    <textarea
                      name="description"
                      value={
                        modalMode === "create"
                          ? formData.description
                          : editFormData.description
                      }
                      onChange={handleFormChange}
                      placeholder="Department description..."
                      rows={4}
                      className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:outline-none focus:border-indigo-500"
                    />
                  </div>

                  <div className="flex gap-3 pt-4">
                    <button
                      type="submit"
                      className="flex-1 bg-indigo-600 hover:bg-indigo-700 text-white font-bold py-2 rounded-lg transition-colors"
                    >
                      {modalMode === "create" ? "Create" : "Update"}
                    </button>
                    <button
                      type="button"
                      onClick={closeModal}
                      className="flex-1 bg-slate-300 hover:bg-slate-400 text-slate-900 font-bold py-2 rounded-lg transition-colors"
                    >
                      Cancel
                    </button>
                  </div>
                </form>
              </div>
            </div>
          </div>
        )}

        {/* Delete Confirmation Modal */}
        {deleteConfirm !== null && (
          <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
            <div className="bg-white rounded-lg shadow-xl max-w-sm w-full mx-4">
              <div className="p-6">
                <h3 className="text-lg font-bold text-slate-900 mb-2">
                  Confirm Delete
                </h3>
                <p className="text-slate-600 mb-4">
                  Are you sure you want to delete the department "
                  {
                    departments.find((d) => d.departmentId === deleteConfirm)
                      ?.name
                  }
                  "? This action cannot be undone.
                </p>
                <div className="flex gap-3">
                  <button
                    onClick={() => {
                      if (deleteConfirm !== null) {
                        handleDelete(deleteConfirm);
                      }
                    }}
                    className="flex-1 bg-red-600 hover:bg-red-700 text-white font-bold py-2 rounded-lg transition-colors"
                  >
                    Delete
                  </button>
                  <button
                    onClick={() => setDeleteConfirm(null)}
                    className="flex-1 bg-slate-300 hover:bg-slate-400 text-slate-900 font-bold py-2 rounded-lg transition-colors"
                  >
                    Cancel
                  </button>
                </div>
              </div>
            </div>
          </div>
        )}
      </main>
    </div>
  );
};

export default Departments;
