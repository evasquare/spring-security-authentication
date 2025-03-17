import { BrowserRouter as Router, Routes, Route } from "react-router";
import Login from "./pages/Login";
import Join from "./pages/Join";
import "normalize.css";
import User from "./pages/User";
import { useEffect, useState } from "react";
import { validateSession } from "./lib/utils";
import ChangePassword from "./pages/ChangePassword";

function App() {
    const [isValidated, setIsValidated] = useState(false);
    useEffect(() => {
        (async () => {
            if (
                window.location.pathname !== "/login" &&
                window.location.pathname !== "/join"
            ) {
                if (!(await validateSession())) {
                    window.location.href = "/login";
                }
            }

            if (await validateSession()) {
                setIsValidated(true);
            }
        })();
    }, []);

    return (
        <>
            <Router>
                <Routes>
                    {isValidated ? (
                        <Route path="/" element={<User />}></Route>
                    ) : null}
                    <Route path="/login" element={<Login />}></Route>
                    <Route path="/join" element={<Join />}></Route>
                    <Route
                        path="/change-password"
                        element={<ChangePassword />}
                    ></Route>
                </Routes>
            </Router>
        </>
    );
}

export default App;
