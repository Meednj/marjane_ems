import React, { useState } from "react";
import { Lock, ArrowRight, User, AlertCircle } from "lucide-react";
import logo from "../assets/mainLogo.png";
import { useNavigate } from "react-router-dom";
import { login } from "../api/auth";

const Login: React.FC = () => {
  const [eid, setEid] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [errorCode, setErrorCode] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  const navigate = useNavigate();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError("");
    setErrorCode(null);
    setIsLoading(true);

    try {
      const data = await login(eid, password);

      localStorage.setItem("token", data.token || "");
      localStorage.setItem("role", data.role || "");

      navigate("/dashboard");
    } catch (err: any) {
      const errorMessage =
        err?.response?.data?.message ||
        "Login failed. Please check your credentials.";
      const code = err?.response?.data?.errorCode || null;
      setError(errorMessage);
      setErrorCode(code);
    } finally {
      setIsLoading(false);
    }
  };
  return (
    <div className="min-h-screen flex items-center justify-center bg-linear-to-br from-slate-50 to-indigo-100 p-4">
      <div className="w-full max-w-md">
        <div className="flex justify-center">
          <img
            src={logo}
            alt="Company Logo"
            className="w-64 h-auto object-contain rounded-lg"
          />
        </div>

        <div className="bg-white shadow-xl rounded-2xl p-10 border border-white/20">
          {/* Header */}
          <div className="text-center mb-10">
            <h2 className="text-2xl font-extrabold text-slate-800 tracking-tight">
              Portal Access
            </h2>
            <p className="text-slate-500 mt-2 text-sm">
              Please enter your details to sign in.
            </p>
          </div>

          <form className="space-y-6" onSubmit={handleSubmit}>
            {/* Error Message */}
            {error && (
              <div className="flex items-center gap-3 bg-red-50 border border-red-200 rounded-xl p-4 text-red-700 animate-shake">
                <AlertCircle className="w-5 h-5 shrink-0" />
                <span className="text-sm font-medium">{error}</span>
              </div>
            )}

            {/* Employee ID */}
            <div className="group">
              <label className="block text-sm font-semibold text-slate-700 mb-2 ml-1">
                Employee ID
              </label>
              <div
                className={`flex items-center border rounded-xl px-4 py-3 bg-slate-50 transition-all focus-within:ring-2 focus-within:ring-indigo-900 focus-within:bg-white focus-within:border-transparent ${
                  errorCode === "INVALID_EID"
                    ? "border-red-400 focus-within:ring-red-400"
                    : "border-slate-200"
                }`}
              >
                <User
                  className={`w-5 h-5 mr-3 transition-colors ${
                    errorCode === "INVALID_EID"
                      ? "text-red-400"
                      : "text-slate-400 group-focus-within:text-indigo-900"
                  }`}
                />
                <input
                  value={eid}
                  onChange={(e) => {
                    setEid(e.target.value);
                    setError("");
                    setErrorCode(null);
                  }}
                  type="text"
                  placeholder="E123..."
                  className="w-full bg-transparent outline-none text-slate-800 placeholder:text-slate-400"
                />
              </div>
            </div>

            {/* Password */}
            <div className="group">
              <label className="block text-sm font-semibold text-slate-700 mb-2 ml-1">
                Password
              </label>
              <div
                className={`flex items-center border rounded-xl px-4 py-3 bg-slate-50 transition-all focus-within:ring-2 focus-within:ring-indigo-900 focus-within:bg-white focus-within:border-transparent ${
                  errorCode === "INVALID_PASSWORD"
                    ? "border-red-400 focus-within:ring-red-400"
                    : "border-slate-200"
                }`}
              >
                <Lock
                  className={`w-5 h-5 mr-3 transition-colors ${
                    errorCode === "INVALID_PASSWORD"
                      ? "text-red-400"
                      : "text-slate-400 group-focus-within:text-indigo-900"
                  }`}
                />
                <input
                  value={password}
                  onChange={(e) => {
                    setPassword(e.target.value);
                    setError("");
                    setErrorCode(null);
                  }}
                  type="password"
                  placeholder="••••••••"
                  className="w-full bg-transparent outline-none text-slate-800"
                />
              </div>
            </div>

            {/* Remember + Forgot */}
            <div className="flex items-center justify-between text-sm">
              <label className="flex items-center gap-2 cursor-pointer group">
                <input
                  type="checkbox"
                  className="w-4 h-4 rounded border-slate-300 text-indigo-600 focus:ring-indigo-900 cursor-pointer"
                />
                <span className="text-slate-600 group-hover:text-slate-800 transition-colors font-medium">
                  Remember me
                </span>
              </label>
              <a
                href="#"
                className="text-indigo-600 hover:text-yellow-400 font-semibold transition-colors"
              >
                Forgot password?
              </a>
            </div>

            {/* Button */}
            <button
              type="submit"
              disabled={isLoading}
              className="group relative w-full bg-slate-900 text-white py-3.5 rounded-xl font-bold hover:bg-slate-800 active:scale-[0.98] transition-all flex items-center justify-center gap-2 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {isLoading ? "Signing In..." : "Sign In"}
              {!isLoading && (
                <ArrowRight className="w-4 h-4 group-hover:translate-x-1 transition-transform" />
              )}
            </button>
          </form>
        </div>
      </div>
    </div>
  );
};

export default Login;
