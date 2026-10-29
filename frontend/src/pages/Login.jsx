import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { api, setSessao } from '../api.js'

export default function Login() {
  const [login, setLogin] = useState('')
  const [senha, setSenha] = useState('')
  const [erro, setErro] = useState('')
  const navigate = useNavigate()

  const entrar = async (e) => {
    e.preventDefault()
    try {
      setSessao(await api('/auth/login', { method: 'POST', body: { login, senha } }))
      navigate('/pedidos')
    } catch (err) { setErro(err.message) }
  }

  return (
    <div className="card auth">
      <h1>Entrar</h1>
      <form onSubmit={entrar}>
        <label>Usuário</label><input required value={login} onChange={(e) => setLogin(e.target.value)} />
        <label>Senha</label><input required type="password" value={senha} onChange={(e) => setSenha(e.target.value)} />
        {erro && <div className="erro">{erro}</div>}
        <p><button type="submit">Entrar</button></p>
      </form>
      <Link to="/registro">Ainda não tenho cadastro</Link>
    </div>
  )
}
