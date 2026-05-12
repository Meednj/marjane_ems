import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import logo from "../assets/hero.png";
import { fetchEmployeeById } from "../api/employeeService";
import { updateUserStatus } from "../api/userService";

interface NavbarProps {
  userRole: string | null;
  title: string;
  subtitle?: string;
}

const getEidFromToken = () => {
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

const Navbar = ({ userRole, title, subtitle }: NavbarProps) => {
  useEffect(() => {
    const previous = document.title;
    document.title = `${title} - Marjane EMS`;
    return () => {
      document.title = previous;
    };
  }, [title]);
  const navigate = useNavigate();
  const [currentUserId, setCurrentUserId] = useState<number | null>(null);
  const [status, setStatus] = useState<"ACTIVE" | "INACTIVE" | "ON_LEAVE">(
    "ACTIVE",
  );
  const [savingStatus, setSavingStatus] = useState(false);

  useEffect(() => {
    const loadCurrentUser = async () => {
      const eid = getEidFromToken();
      if (!eid) return;

      try {
        const employee = await fetchEmployeeById(eid);
        if (employee.id) {
          setCurrentUserId(employee.id);
          setStatus(
            employee.status?.toUpperCase() === "ON_LEAVE"
              ? "ON_LEAVE"
              : employee.status?.toUpperCase() === "ACTIVE"
                ? "ACTIVE"
                : "INACTIVE",
          );
        }
      } catch (error) {
        console.error("Error loading current user:", error);
      }
    };

    loadCurrentUser();
  }, []);

  const handleLogout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("role");
    navigate("/login");
  };

  const handleStatusChange = async (value: string) => {
    if (
      !currentUserId ||
      status === "ON_LEAVE" ||
      (value !== "ACTIVE" && value !== "INACTIVE")
    ) {
      return;
    }

    const nextStatus = value as "ACTIVE" | "INACTIVE";
    const previousStatus = status;

    setStatus(nextStatus);
    setSavingStatus(true);

    try {
      await updateUserStatus(nextStatus);
    } catch (error) {
      console.error("Error updating status:", error);
      setStatus(previousStatus);
    } finally {
      setSavingStatus(false);
    }
  };

  return (
    <header className="border-b border-slate-200 bg-white/95 text-slate-900 shadow-sm backdrop-blur">
      <div className="mx-auto max-w-7xl px-4 py-5 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between gap-4">
          <div className="flex items-center gap-3 flex-1">
            <img src={logo} alt="Marjane EMS" className="h-15 w-auto mr-5" />
            <div>
              <h1 className="text-2xl sm:text-3xl font-black tracking-tight text-slate-900">
                {title}
              </h1>
              {subtitle && <p className="text-sm text-slate-500">{subtitle}</p>}
            </div>
          </div>

          <div className="flex flex-wrap items-center justify-end gap-3">
            {userRole && (
              <div className="text-right">
                <p className="text-sm font-medium text-slate-700">
                  Role:{" "}
                  <span className="ml-2 inline-block rounded-full bg-slate-100 px-3 py-1 text-xs font-semibold capitalize text-slate-800 ring-1 ring-slate-200">
                    {userRole}
                  </span>
                </p>
              </div>
            )}

            {currentUserId && (
              <label className="flex items-center gap-2 rounded-lg border border-slate-200 bg-slate-50 px-3 py-2 text-sm font-semibold text-slate-700">
                Status
                <select
                  value={status}
                  onChange={(event) => handleStatusChange(event.target.value)}
                  disabled={savingStatus || status === "ON_LEAVE"}
                  className="bg-transparent text-sm font-semibold text-slate-900 outline-none disabled:opacity-60"
                >
                  <option value="ACTIVE">Active</option>
                  <option value="INACTIVE">Inactive</option>
                  <option value="ON_LEAVE" aria-readonly>On Leave</option>
                </select>
              </label>
            )}

            <div className="flex gap-2">
              <button
                onClick={() => navigate("/dashboard")}
                className="rounded-lg border border-slate-200 px-4 py-2 text-sm font-semibold text-slate-700 transition-colors hover:bg-slate-100"
              >
                Dashboard
              </button>
              <button
                onClick={handleLogout}
                className="rounded-lg bg-rose-600 px-4 py-2 text-sm font-semibold text-white transition-colors hover:bg-rose-500"
              >
                Logout
              </button>
            </div>
          </div>
        </div>
      </div>
    </header>
  );
};

export default Navbar;
