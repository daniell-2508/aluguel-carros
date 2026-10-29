import React from 'react'
import { createRoot } from 'react-dom/client'
import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom'
import Layout from './Layout.jsx'
import Login from './pages/Login.jsx'
import Registro from './pages/Registro.jsx'
import Pedidos from './pages/Pedidos.jsx'
import Automoveis from './pages/Automoveis.jsx'
import Clientes from './pages/Clientes.jsx'
import { getSessao } from './api.js'
import './styles.css'

function Protegida({ children }) {
  return getSessao() ? children : <Navigate to="/login" replace />
}

createRoot(document.getElementById('root')).render(
  <BrowserRouter>
    <Routes>
      <Route path="/login" element={<Login />} />
      <Route path="/registro" element={<Registro />} />
      <Route element={<Protegida><Layout /></Protegida>}>
        <Route path="/pedidos" element={<Pedidos />} />
        <Route path="/automoveis" element={<Automoveis />} />
        <Route path="/clientes" element={<Clientes />} />
      </Route>
      <Route path="*" element={<Navigate to="/pedidos" replace />} />
    </Routes>
  </BrowserRouter>
)
