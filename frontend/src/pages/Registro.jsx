import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { api, setSessao } from '../api.js'
import ClienteForm from '../components/ClienteForm.jsx'

export default function Registro() {
  const [tipo, setTipo] = useState('CLIENTE')
  const [login, setLogin] = useState('')
  const [senha, setSenha] = useState('')
  const [agente, setAgente] = useState({ nome: '', cnpj: '', tipo: 'BANCO' })
  const [erro, setErro] = useState('')
  const navigate = useNavigate()

  const concluir = async (caminho, body) => {
    try {
      setSessao(await api(caminho, { method: 'POST', body }))
      navigate('/pedidos')
    } catch (err) { setErro(err.message) }
  }

  return (
    <div className="card auth" style={{ maxWidth: 560 }}>
      <h1>Cadastro</h1>
      <label>Tipo de usuário</label>
      <select value={tipo} onChange={(e) => setTipo(e.target.value)}>
        <option value="CLIENTE">Cliente (pessoa física)</option>
        <option value="AGENTE">Agente (empresa ou banco)</option>
      </select>
      <div className="linha">
        <div><label>Usuário</label><input required value={login} onChange={(e) => setLogin(e.target.value)} /></div>
        <div><label>Senha (mín. 6)</label><input required type="password" value={senha} onChange={(e) => setSenha(e.target.value)} /></div>
      </div>
      {erro && <div className="erro">{erro}</div>}

      {tipo === 'CLIENTE' ? (
        <ClienteForm rotulo="Cadastrar"
          onSubmit={(dados) => concluir('/auth/registro/cliente', { login, senha, dados })} />
      ) : (
        <form onSubmit={(e) => { e.preventDefault(); concluir('/auth/registro/agente', { login, senha, ...agente }) }}>
          <label>Nome</label><input required value={agente.nome} onChange={(e) => setAgente({ ...agente, nome: e.target.value })} />
          <label>CNPJ</label><input required value={agente.cnpj} onChange={(e) => setAgente({ ...agente, cnpj: e.target.value })} />
          <label>Tipo</label>
          <select value={agente.tipo} onChange={(e) => setAgente({ ...agente, tipo: e.target.value })}>
            <option value="BANCO">Banco</option>
            <option value="EMPRESA">Empresa</option>
          </select>
          <p><button type="submit">Cadastrar</button></p>
        </form>
      )}
      <Link to="/login">Voltar ao login</Link>
    </div>
  )
}
