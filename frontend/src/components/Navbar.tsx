import { useNavigate } from "react-router-dom";
import logo from "../assets/mainLogo.png";

interface NavbarProps {
  userRole: string | null;
  title: string;
  subtitle?: string;
}

const Navbar = ({ userRole, title, subtitle }: NavbarProps) => {
  const navigate = useNavigate();

  const handleLogout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("role");
    navigate("/login");
  };

  return (
    <header className="bg-white shadow-lg border-b-4 border-indigo-600">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6">
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-3 flex-1">
            <img src={logo} alt="Marjane EMS" className="h-10 w-auto" />
            <div>
              <h1 className="text-3xl font-bold text-gray-900">{title}</h1>
              {subtitle && <p className="text-sm text-gray-600">{subtitle}</p>}
            </div>
          </div>
          <div className="flex items-center gap-4">
            {userRole && (
              <div className="text-right">
                <p className="text-sm font-medium text-gray-900">
                  Role:{" "}
                  <span className="inline-block px-3 py-1 bg-indigo-100 text-indigo-800 rounded-full text-xs font-semibold capitalize ml-2">
                    {userRole}
                  </span>
                </p>
              </div>
            )}
            <div className="flex gap-2">
              <button
                onClick={() => navigate("/dashboard")}
                className="px-4 py-2 text-sm font-medium text-gray-700 hover:text-indigo-600 transition-colors"
              >
                Dashboard
              </button>
              <button
                onClick={handleLogout}
                className="px-4 py-2 bg-red-600 text-white rounded-lg text-sm font-medium hover:bg-red-700 transition-colors"
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
