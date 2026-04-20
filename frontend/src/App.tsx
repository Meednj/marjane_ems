import { Routes, Route } from "react-router-dom";

import type { JSX } from "react/jsx-dev-runtime";
import Login from "./components/Login";
import { Dashboard } from "./components/Dashboard";

function App(): JSX.Element {
  return (
    <Routes>
      <Route path="/" element={<Login />} />
      <Route path="/login" element={<Login />} />
      <Route path="/dashboard" element={<Dashboard />} />
    </Routes>
  );
}

export default App;
