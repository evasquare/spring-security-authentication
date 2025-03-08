import { BrowserRouter as Router, Routes, Route } from "react-router";
import Home from "./Home";
import UserPage from "./User";
import Join from "./Join";

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
