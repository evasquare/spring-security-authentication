import { BrowserRouter as Router, Routes, Route } from "react-router";
import Home from "./pages/Home";
import UserPage from "./pages/User";
import Join from "./pages/Join";
import "normalize.css";

function App() {
    return (
        <>
            <Router>
                <Routes>
                    <Route path="/" element={<Home />}></Route>
                    <Route path="/user" element={<UserPage />}></Route>
                    <Route path="/join" element={<Join />}></Route>
                </Routes>
            </Router>
        </>
    );
}

export default App;
