import React from "react";
import { BrowserRouter as Router, Routes, Route, Navigate } from "react-router-dom";
import "./App.css";
import First from "./pages/First";
import Second from "./pages/Second";
import Third from "./pages/Third";
import Fourth from "./pages/Fourth";
import Header from "./component/Header";
import BottomNav from "./component/BottomNav";

function App() {
  return (
    <Router>
      <div className="App">
        <Header />
        <Routes>
          {/* 기본 경로('/')에서 자동으로 '/first'로 이동 */}
          <Route path="/" element={<Navigate to="/first" />} />
          <Route path="/first" element={<First />} />
          <Route path="/second" element={<Second />} />
          <Route path="/third" element={<Third />} />
          <Route path="/fourth" element={<Fourth />} />
        </Routes>
        <BottomNav />
      </div>
    </Router>
  );
}

export default App;
