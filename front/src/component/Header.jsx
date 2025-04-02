import React from "react";
import AppBar from "@mui/material/AppBar";
import Toolbar from "@mui/material/Toolbar";
import Box from "@mui/material/Box";
import InputBase from "@mui/material/InputBase";
import SearchIcon from "@mui/icons-material/Search";
import logo from "../img/logo.png";
import "./style/Header.css"; // CSS 파일 추가

function Header() {
  return (
    <AppBar position="static" color="transparent" elevation={1}>
      <Toolbar className="header-toolbar">
        {/* 로고 */}
        <Box className="logo">
          <img src={logo} alt="로고" />
        </Box>

        {/* 검색창 */}
        <Box className="search-container">
          <SearchIcon className="search-icon" />
          <InputBase
            className="search-input"
            placeholder="검색…"
            inputProps={{ "aria-label": "search" }}
          />
        </Box>

        {/* 프로필 자리 */}
        <Box className="profile"></Box>
      </Toolbar>
    </AppBar>
  );
}

export default Header;
