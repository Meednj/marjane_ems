import React from "react";
import { Lock, ArrowRight, User } from "lucide-react";
import logo from "../assets/mainLogo.png";

const Login: React.FC = () => {
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

        <div className="bg-white shadow-xl rounded-3xl p-10 border border-white/20">
          {/* Header */}
          <div className="text-center mb-10">
            <h2 className="text-2xl font-extrabold text-slate-800 tracking-tight">
              Portal Access
            </h2>
            <p className="text-slate-500 mt-2 text-sm">
              Please enter your details to sign in.
            </p>
          </div>

          <form className="space-y-6" onSubmit={(e) => e.preventDefault()}>
            {/* Employee ID */}
            <div className="group">
              <label className="block text-sm font-semibold text-slate-700 mb-2 ml-1">
                Employee ID
              </label>
              <div className="flex items-center border border-slate-200 rounded-xl px-4 py-3 bg-slate-50 transition-all focus-within:ring-2 focus-within:ring-indigo-900 focus-within:bg-white focus-within:border-transparent">
                <User className="w-5 h-5 text-slate-400 mr-3 group-focus-within:text-indigo-900 transition-colors" />
                <input
                  type="text"
                  placeholder="E12345678"
                  className="w-full bg-transparent outline-none text-slate-800 placeholder:text-slate-400"
                />
              </div>
            </div>

            {/* Password */}
            <div className="group">
              <label className="block text-sm font-semibold text-slate-700 mb-2 ml-1">
                Password
              </label>
              <div className="flex items-center border border-slate-200 rounded-xl px-4 py-3 bg-slate-50 transition-all focus-within:ring-2 focus-within:ring-indigo-900 focus-within:bg-white focus-within:border-transparent">
                <Lock className="w-5 h-5 text-slate-400 mr-3 group-focus-within:text-indigo-900 transition-colors" />
                <input
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
              className="group relative w-full bg-slate-900 text-white py-3.5 rounded-xl font-bold hover:bg-slate-800 active:scale-[0.98] transition-all flex items-center justify-center gap-2"
            >
              Sign In
              <ArrowRight className="w-4 h-4 group-hover:translate-x-1 transition-transform" />
            </button>
          </form>
        </div>
      </div>
    </div>
  );
};

export default Login;
