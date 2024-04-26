import React from "react";
import styles from "./Header.module.css";
const Header = () => {
  return (
    <header className={styles.header}>
      <p className={styles.title}>QQueueing Admin Dashboard</p>
    </header>
  );
};

export default Header;