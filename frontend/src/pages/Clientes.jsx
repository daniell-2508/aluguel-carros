import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { api, getSessao, setSessao } from '../api.js'
import ClienteForm from '../components/ClienteForm.jsx'

/** Cliente: gerencia o proprio cadastro. Agente: lista, edita e exclui qualquer cliente. */
export default function Clientes() {
  const ehCliente = getSessao().perfil === 'CLIENTE'
  const [lista, setLista] = useState([])
  const [edicao, setEdicao] = useState(null)
  const [msg, setMsg] = useState({ tipo: '', texto: '' })
  const navigate = useNavigate()

  const carregar = async () => {
    try {
      setLista(ehCliente ? [await api('/clientes/me')] : await api('/clientes'))
    } catch (e) { setMsg({ tipo: 'erro', texto: e.message }) }
  }
  useEffect(() => { carregar() }, [])

  const salvar = async (dados) => {
    try {
      await api(`/clientes/${edicao.id}`, { method: 'PUT', body: dados })
      setEdicao(null)
      setMsg({ tipo: 'ok', texto: 'Cadastro atualizado.' })
      carregar()
    } catch (e) { setMsg({ tipo: 'erro', texto: e.message }) }
  }

  const excluir = async (c) => {
    if (!confirm(`Excluir o cadastro de ${c.nome}?`)) return
    try {
      await api(`/clientes/${c.id}`, { method: 'DELETE' })
      if (ehCliente) { setSessao(null); navigate('/login') } else carregar()
    } catch (e) { setMsg({ tipo: 'erro', texto: e.message }) }
  }

  return (
    <>
      <div className="card">
        <h2>{ehCliente ? 'Meu cadastro' : 'Clientes'}</h2>
        {msg.texto && <div className={msg.tipo}>{msg.texto}</div>}
        <table>
          <thead><tr><th>Nome</th><th>CPF</th><th>RG</th><th>Profissão</th><th>Rendimentos</th><th /></tr></thead>
          <tbody>
            {lista.map((c) => (
              <tr key={c.id}>
                <td>{c.nome}<br /><small>{c.login}</small></td>
                <td>{c.cpf}</td><td>{c.rg}</td><td>{c.profissao}</td>
                <td>{c.rendimentos.map((r) => `${r.entidadeEmpregadora}: R$ ${r.valor}`).join('; ')}</td>
                <td className="acoes">
                  <button onClick={() => setEdicao(c)}>Editar</button>
                  <button className="perigo" onClick={() => excluir(c)}>Excluir</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
      {edicao && (
        <div className="card">
          <h2>Editar cliente</h2>
          <ClienteForm key={edicao.id} inicial={edicao} onSubmit={salvar} />
          <button className="sec" onClick={() => setEdicao(null)}>Cancelar</button>
        </div>
      )}
    </>
  )
}
