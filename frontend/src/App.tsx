import { useEffect, useState } from "react";
import { getHello } from "./services/api";

function App() {
  const [message, setMessage] = useState<string>("Loading...");

  useEffect(() => {
    getHello()
      .then((data) => setMessage(data))
      .catch((err) => {
        console.error(err);
        setMessage("Error connecting to backend");
      });
  }, []);

  return (
    <div>
      <h1>React + Spring Boot + TypeScript</h1>
      <p>{message}</p>
    </div>
  );
}

export default App;