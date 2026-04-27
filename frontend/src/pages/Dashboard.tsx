import { useEffect, useState, type JSX } from "react";
import { getRole } from "../api/auth";
import {
  fetchDashboardStats,
  type DashboardData,
} from "../api/dashboardService";
import logo from "../assets/mainLogo.png";

const getToken = () => localStorage.getItem("token");

const Dashboard = () => {
  const [userRole, setUserRole] = useState<string | null>(null);
  const [stats, setStats] = useState<DashboardData>({
    totalUsers: 0,
    totalTickets: 0,
    openTickets: 0,
  });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchData = async () => {
      setLoading(true);
      setError(null);
      try {
        const role = getRole();
        const token = getToken();

        // Check if user is authenticated
        if (!token || !role) {
          setError("Please log in to view the dashboard.");
          setLoading(false);
          return;
        }

        setUserRole(role);

        // Only fetch data if user is admin
        if (role.toLowerCase().includes("admin")) {
          const dashboardData = await fetchDashboardStats();
          setStats(dashboardData);
        } else {
          // For non-admin users
          setError(
            `Limited access: Some statistics are only available to administrators. You are logged in as ${role}.`,
          );
          setStats({
            totalUsers: 0,
            totalTickets: 0,
            openTickets: 0,
        
          });
        }
      } catch (err) {
        console.error("Error fetching dashboard data:", err);
        setError(
          "Failed to load dashboard data. Please ensure you are logged in and have the required permissions.",
        );
        setStats({
          totalUsers: 0,
          totalTickets: 0,
          openTickets: 0,
          
        });
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, []);

  const role = userRole?.toLowerCase();

  const permissions = {
    admin: {
      canViewAllTickets: true,
      canManageTickets: true,
      canViewAssigned: true,
      canManageEmployees: true,
      canViewReports: true,
      canManageDepartments: true,
    },
    technician: {
      canViewAllTickets: true,
      canManageTickets: true,
      canViewAssigned: true,
      canManageEmployees: false,
      canViewReports: false,
      canManageDepartments: false,
    },
    employee: {
      canViewAllTickets: true,
      canManageTickets: false,
      canViewAssigned: false,
      canManageEmployees: false,
      canViewReports: false,
      canManageDepartments: false,
    },
  };

  const userPermissions = permissions[role as keyof typeof permissions];

  interface StatCard {
    icon: JSX.Element;
    label: string;
    value: number | string;
    bgColor: string;
    textColor: string;
  }

  const statCards: StatCard[] = [
    {
      icon: (
        <svg
          xmlns="http://www.w3.org/2000/svg"
          fill="none"
          viewBox="0 0 24 24"
          strokeWidth={1.5}
          stroke="currentColor"
          className="w-6 h-6"
        >
          <path
            strokeLinecap="round"
            strokeLinejoin="round"
            d="M15 19.128a9.38 9.38 0 0 0 2.625.372 9.337 9.337 0 0 0 4.121-.952 4.125 4.125 0 0 0-7.533-2.493M15 19.128v-.003c0-1.657-1.338-3-2.991-3H5.009c-1.653 0-2.991 1.343-2.991 3v.006"
          />
        </svg>
      ),
      label: "Total Users",
      value: stats.totalUsers || "—",
      bgColor: "bg-blue-50",
      textColor: "text-blue-600",
    },
    {
      icon: (
        <svg
          xmlns="http://www.w3.org/2000/svg"
          fill="none"
          viewBox="0 0 24 24"
          strokeWidth={1.5}
          stroke="currentColor"
          className="w-6 h-6"
        >
          <path
            strokeLinecap="round"
            strokeLinejoin="round"
            d="M16.5 6v.75m0 3v.75m0 3v.75m0 3V18m-9-5.25h5.25M7.5 15h3M3.375 5.25c-.621 0-1.125.504-1.125 1.125v3.026a2.999 2.999 0 0 1 0 5.198v3.026c0 .621.504 1.125 1.125 1.125h17.25c.621 0 1.125-.504 1.125-1.125v-3.026a2.999 2.999 0 0 1 0-5.198V6.375c0-.621-.504-1.125-1.125-1.125H3.375Z"
          />
        </svg>
      ),
      label: "Total Tickets",
      value: stats.totalTickets ?? "—",
      bgColor: "bg-indigo-50",
      textColor: "text-indigo-600",
    },
    {
      icon: (
        <svg
          xmlns="http://www.w3.org/2000/svg"
          fill="none"
          viewBox="0 0 24 24"
          strokeWidth={1.5}
          stroke="currentColor"
          className="w-6 h-6"
        >
          <path
            strokeLinecap="round"
            strokeLinejoin="round"
            d="M12 8.25v-1.5m0 1.5c-1.355 0-2.697.056-4.024.166C6.845 8.51 6 9.473 6 10.608v2.1m0 0h12m-12 0v5.25m0-5.25v-5.25m12 5.25v5.25m0-5.25v-5.25m0-3.408c.722.127 1.443.26 2.157.41C18.335 7.39 19 8.54 19 9.75v2.1M4.863 12.893c.358.37.915.6 1.637.625a2.122 2.122 0 0 0 2.16-2.125v-1.5m-4.040 2.5h.002Z"
          />
        </svg>
      ),
      label: "Open Tickets",
      value: stats.openTickets ?? "—",
      bgColor: "bg-orange-50",
      textColor: "text-orange-600",
    },
    {
      icon: (
        <svg
          xmlns="http://www.w3.org/2000/svg"
          fill="none"
          viewBox="0 0 24 24"
          strokeWidth={1.5}
          stroke="currentColor"
          className="w-6 h-6"
        >
          <path
            strokeLinecap="round"
            strokeLinejoin="round"
            d="M3.75 3v11.25A2.25 2.25 0 0 0 6 16.5h2.25M3.75 3h-1.5m1.5 0h16.5m0 0A2.25 2.25 0 0 1 21.75 5.25v1.5M3.75 3a2.25 2.25 0 0 0-2.25 2.25v1.5m0 0h16.5m-16.5 0v11.25A2.25 2.25 0 0 0 6 20.25h12A2.25 2.25 0 0 0 20.25 18V8.25m0 0H3.75m16.5 0v3H3.75m16.5-3v3"
          />
        </svg>
      ),
      label: "Departments",
      value: stats.departmentsCount || "—",
      bgColor: "bg-emerald-50",
      textColor: "text-emerald-600",
    },
    {
      icon: (
        <svg
          xmlns="http://www.w3.org/2000/svg"
          fill="none"
          viewBox="0 0 24 24"
          strokeWidth={1.5}
          stroke="currentColor"
          className="w-6 h-6"
        >
          <path
            strokeLinecap="round"
            strokeLinejoin="round"
            d="M9 12.75L11.25 15 15 9.75M21 12a9 9 0 1 1-18 0 9 9 0 0 1 18 0Z"
          />
        </svg>
      ),
      label: "Pending Leaves",
      value: stats.pendingLeaves || "—",
      bgColor: "bg-green-50",
      textColor: "text-green-600",
    },
    {
      icon: (
        <svg
          xmlns="http://www.w3.org/2000/svg"
          fill="none"
          viewBox="0 0 24 24"
          strokeWidth={1.5}
          stroke="currentColor"
          className="w-6 h-6"
        >
          <path
            strokeLinecap="round"
            strokeLinejoin="round"
            d="M6.75 3v2.25M17.25 3v2.25M3 18.75V7.5a2.25 2.25 0 0 1 2.25-2.25h13.5A2.25 2.25 0 0 1 21 7.5v11.25m-18 0A2.25 2.25 0 0 0 5.25 21h13.5A2.25 2.25 0 0 0 21 18.75m-18 0v-5.25m0 0v-2.25m0 2.25h18m-13.5-2.25h2.25m-2.25 0h-4.5"
          />
        </svg>
      ),
      label: "Total Leaves",
      value: stats.totalLeaves || "—",
      bgColor: "bg-rose-50",
      textColor: "text-rose-600",
    },
  ];

  interface FeatureCard {
    title: string;
    description: string;
    icon: JSX.Element;
    link: string;
    color: string;
    permission: boolean;
  }

  const featureCards: FeatureCard[] = [
    {
      title: "Manage Employees",
      description: "Add, edit, and manage employee information and records",
      icon: (
        <svg
          xmlns="http://www.w3.org/2000/svg"
          fill="none"
          viewBox="0 0 24 24"
          strokeWidth={1.5}
          stroke="currentColor"
          className="w-8 h-8"
        >
          <path
            strokeLinecap="round"
            strokeLinejoin="round"
            d="M12 4.5v15m7.5-7.5h-15"
          />
        </svg>
      ),
      link: "/employees",
      color: "border-blue-500",
      permission: userPermissions?.canManageEmployees || false,
    },
    {
      title: "All Tickets",
      description: "View and manage support tickets from all users",
      icon: (
        <svg
          xmlns="http://www.w3.org/2000/svg"
          fill="none"
          viewBox="0 0 24 24"
          strokeWidth={1.5}
          stroke="currentColor"
          className="w-8 h-8"
        >
          
          <path
            strokeLinecap="round"
            strokeLinejoin="round"
            d="M16.5 6v.75m0 3v.75m0 3v.75m0 3V18m-9-5.25h5.25M7.5 15h3M3.375 5.25c-.621 0-1.125.504-1.125 1.125v3.026a2.999 2.999 0 0 1 0 5.198v3.026c0 .621.504 1.125 1.125 1.125h17.25c.621 0 1.125-.504 1.125-1.125v-3.026a2.999 2.999 0 0 1 0-5.198V6.375c0-.621-.504-1.125-1.125-1.125H3.375Z"
          />
        </svg>
      ),
      link: "/tickets",
      color: "border-indigo-500",
      permission: userPermissions?.canViewAllTickets || false,
    },
    
    {
      
      title: "Assigned Tickets",
      description: "View tickets assigned to you",
      icon: (
        <svg
          xmlns="http://www.w3.org/2000/svg"
          fill="none"
          viewBox="0 0 24 24"
          strokeWidth={1.5}
          stroke="currentColor"
          className="w-8 h-8"
        >
          <path
            strokeLinecap="round"
            strokeLinejoin="round"
            d="M9 12.75L11.25 15 15 9.75M21 12a9 9 0 1 1-18 0 9 9 0 0 1 18 0Z"
          />
        </svg>
      ),
      link: "/assigned",
      color: "border-green-500",
      permission: userPermissions?.canViewAssigned || false,
    },
    {
      title: "Reports & Analytics",
      description: "View performance and analytics reports",
      icon: (
        <svg
          xmlns="http://www.w3.org/2000/svg"
          fill="none"
          viewBox="0 0 24 24"
          strokeWidth={1.5}
          stroke="currentColor"
          className="w-8 h-8"
        >
          <path
            strokeLinecap="round"
            strokeLinejoin="round"
            d="M3 13.125C3 12.504 3.504 12 4.125 12h2.25c.621 0 1.125.504 1.125 1.125v6.75C7.5 20.496 6.996 21 6.375 21h-2.25A1.125 1.125 0 0 1 3 19.875v-6.75Zm9-2.625c-.621 0-1.125.504-1.125 1.125v8.25c0 .621.504 1.125 1.125 1.125h2.25c.621 0 1.125-.504 1.125-1.125v-8.25c0-.621-.504-1.125-1.125-1.125h-2.25Zm9-2.625c-.621 0-1.125.504-1.125 1.125v11.25c0 .621.504 1.125 1.125 1.125h2.25c.621 0 1.125-.504 1.125-1.125V9c0-.621-.504-1.125-1.125-1.125h-2.25Z"
          />
        </svg>
      ),
      link: "/reports",
      color: "border-purple-500",
      permission: userPermissions?.canViewReports || false,
    },
    {
      title: "Manage Departments",
      description: "Manage organizational departments and structure",
      icon: (
        <svg
          xmlns="http://www.w3.org/2000/svg"
          fill="none"
          viewBox="0 0 24 24"
          strokeWidth={1.5}
          stroke="currentColor"
          className="w-8 h-8"
        >
          <path
            strokeLinecap="round"
            strokeLinejoin="round"
            d="M20.25 6.375c0 2.278-3.694 4.125-8.25 4.125S3.75 8.653 3.75 6.375m16.5 0c0-2.278-3.694-4.125-8.25-4.125S3.75 4.097 3.75 6.375m16.5 0v11.25c0 2.278-3.694 4.125-8.25 4.125s-8.25-1.847-8.25-4.125V6.375"
          />
        </svg>
      ),
      link: "/departments",
      color: "border-cyan-500",
      permission: userPermissions?.canManageDepartments || false,
    },
    {
      title: "Manage Tickets",
      description: "Create and manage support tickets",
      icon: (
        <svg
          xmlns="http://www.w3.org/2000/svg"
          fill="none"
          viewBox="0 0 24 24"
          strokeWidth={1.5}
          stroke="currentColor"
          className="w-8 h-8"
        >
          <path
            strokeLinecap="round"
            strokeLinejoin="round"
            d="M2.25 12.75V12A2.25 2.25 0 0 1 4.5 9.75h15A2.25 2.25 0 0 1 21.75 12v.75m-8.69-6.44-2.12-2.12a1.5 1.5 0 0 0-1.061-.44H4.5A2.25 2.25 0 0 0 2.25 6v12a2.25 2.25 0 0 0 2.25 2.25h15A2.25 2.25 0 0 0 21.75 18V9a2.25 2.25 0 0 0-2.25-2.25h-5.379a1.5 1.5 0 0 1-1.06-.44Z"
          />
        </svg>
      ),
      link: "/manage-tickets",
      color: "border-amber-500",
      permission: userPermissions?.canManageTickets || false,
    },
  ];
  const visibleFeatures = featureCards.filter((card) => card.permission);

  return (
    <div className="min-h-screen bg-linear-to-br from-slate-900 via-slate-800 to-slate-900">
      {/* Header */}
      <header className="bg-white shadow-lg border-b-4 border-indigo-600">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-3">
              <img src={logo} alt="Marjane EMS" className="h-10 w-auto" />
              <div>
                <h1 className="text-3xl font-bold text-gray-900">
                  Administration Dashboard
                </h1>
                <p className="text-sm text-gray-600">
                  Employee Management System
                </p>
              </div>
            </div>
            <div className="text-right">
              <p className="text-sm font-medium text-gray-900">
                {userRole ? (
                  <>
                    Role:{" "}
                    <span className="inline-block px-3 py-1 bg-indigo-100 text-indigo-800 rounded-full text-xs font-semibold capitalize">
                      {userRole}
                    </span>
                  </>
                ) : (
                  <span className="text-red-600 font-semibold">
                    Not authenticated
                  </span>
                )}
              </p>
            </div>
          </div>
        </div>
      </header>

      {/* Main Content */}
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
                Authorization Required
              </p>
              <p className="text-sm text-yellow-800">{error}</p>
              {error.includes("log in") && (
                <p className="text-xs text-yellow-700 mt-2">
                  Please log in to continue.
                </p>
              )}
            </div>
          </div>
        )}

        {/* Loading State */}
        {loading && (
          <div className="flex items-center justify-center py-12">
            <div className="text-center">
              <div className="inline-block animate-spin rounded-full h-12 w-12 border-b-2 border-indigo-600 mb-4"></div>
              <p className="text-white">Loading dashboard data...</p>
            </div>
          </div>
        )}

        {!loading && (
          <>
            {/* Stats Cards */}
            <div className="mb-12">
              <h2 className="text-2xl font-bold text-white mb-6 flex items-center gap-2">
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
                    d="M3 13.125C3 12.504 3.504 12 4.125 12h2.25c.621 0 1.125.504 1.125 1.125v6.75C7.5 20.496 6.996 21 6.375 21h-2.25A1.125 1.125 0 0 1 3 19.875v-6.75Zm9-2.625c-.621 0-1.125.504-1.125 1.125v8.25c0 .621.504 1.125 1.125 1.125h2.25c.621 0 1.125-.504 1.125-1.125v-8.25c0-.621-.504-1.125-1.125-1.125h-2.25Zm9-2.625c-.621 0-1.125.504-1.125 1.125v11.25c0 .621.504 1.125 1.125 1.125h2.25c.621 0 1.125-.504 1.125-1.125V9c0-.621-.504-1.125-1.125-1.125h-2.25Z"
                  />
                </svg>
                Key Metrics
              </h2>
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                {statCards.map((stat, index) => (
                  <div
                    key={index}
                    className={`${stat.bgColor} rounded-lg p-6 border-l-4 ${stat.textColor} shadow-md hover:shadow-lg transition-shadow`}
                  >
                    <div className="flex items-center justify-between">
                      <div>
                        <p className="text-sm font-medium text-gray-600 mb-2">
                          {stat.label}
                        </p>
                        <p className="text-3xl font-bold text-gray-900">
                          {stat.value}
                        </p>
                      </div>
                      <div className={`${stat.textColor} opacity-20`}>
                        {stat.icon}
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            </div>

            {/* Feature Cards */}
            <div>
              <h2 className="text-2xl font-bold text-white mb-6 flex items-center gap-2">
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
                    d="M9 6.75V15m6-6v8.25m.503-6.97l.75-.75a2.25 2.25 0 1 0-3.182-3.182l-.75.75m0 0a2.251 2.251 0 0 0-3.182 3.182l.75.75"
                  />
                </svg>
                Available Tools
              </h2>
              {visibleFeatures.length === 0 ? (
                <div className="text-center py-12">
                  <p className="text-gray-400 text-lg">
                    No features available for your role
                  </p>
                </div>
              ) : (
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                  {visibleFeatures.map((feature, index) => (
                    <a
                      key={index}
                      href={feature.link}
                      className={`bg-white rounded-lg shadow-md hover:shadow-xl transition-all duration-300 p-6 border-t-4 ${feature.color} hover:scale-105 cursor-pointer`}
                    >
                      <div className="flex items-start gap-4">
                        <div
                          className={`${feature.color.replace("border-", "text-")} mt-1`}
                        >
                          {feature.icon}
                        </div>
                        <div className="flex-1">
                          <h3 className="text-lg font-semibold text-gray-900 mb-2">
                            {feature.title}
                          </h3>
                          <p className="text-sm text-gray-600">
                            {feature.description}
                          </p>
                          <div className="mt-4 flex items-center text-sm font-medium text-indigo-600 hover:text-indigo-700">
                            Access Tool
                            <svg
                              xmlns="http://www.w3.org/2000/svg"
                              fill="none"
                              viewBox="0 0 24 24"
                              strokeWidth={2}
                              stroke="currentColor"
                              className="w-4 h-4 ml-2"
                            >
                              <path
                                strokeLinecap="round"
                                strokeLinejoin="round"
                                d="M13.5 4.5L21 12m0 0l-7.5 7.5M21 12H3"
                              />
                            </svg>
                          </div>
                        </div>
                      </div>
                    </a>
                  ))}
                </div>
              )}
            </div>
          </>
        )}
      </main>

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

export default Dashboard;
