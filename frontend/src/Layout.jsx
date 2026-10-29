import { NavLink, Outlet, useNavigate } from 'react-router-dom'
import { getSessao, setSessao } from './api.js'

export default function Layout() {
  const sessao = getSessao()
  const navigate = useNavigate()
  const sair = () => { setSessao(null); navigate('/login') }

  return (
    <>
      <header className="topo">
        <strong>Aluguel de Carros</strong>
        <nav>
          <NavLink to="/pedidos">Pedidos</NavLink>
          <NavLink to="/automoveis">Automóveis</NavLink>
          <NavLink to="/clientes">{sessao.perfil === 'CLIENTE' ? 'Meu cadastro' : 'Clientes'}</NavLink>
        </nav>
        <span>{sessao.nome} ({sessao.perfil}) <button onClick={sair}>Sair</button></span>
      </header>
      <main><Outlet /></main>
    </>
  )
}
