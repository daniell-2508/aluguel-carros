const BASE = import.meta.env.VITE_API_URL || 'http://localhost:8080/api'

export const getSessao = () => {
  try { return JSON.parse(localStorage.getItem('sessao')) } catch { return null }
}
export const setSessao = (s) =>
  s ? localStorage.setItem('sessao', JSON.stringify(s)) : localStorage.removeItem('sessao')

export async function api(caminho, { method = 'GET', body } = {}) {
  const sessao = getSessao()
  const res = await fetch(BASE + caminho, {
    method,
    headers: {
      'Content-Type': 'application/json',
      ...(sessao ? { Authorization: `Bearer ${sessao.token}` } : {}),
    },
    body: body ? JSON.stringify(body) : undefined,
  })
  if (res.status === 401 && sessao) {
    setSessao(null)
    window.location.href = '/login'
  }
  if (!res.ok) {
    const dados = await res.json().catch(() => ({}))
    throw new Error(dados.erro || `Erro ${res.status}`)
  }
  return res.status === 204 ? null : res.json()
}
