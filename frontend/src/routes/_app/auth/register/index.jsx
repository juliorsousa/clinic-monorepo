import { createFileRoute } from '@tanstack/react-router'
import { RegisterForm } from './-components/register-form'

export const Route = createFileRoute('/_app/auth/register/')({
  component: RegisterForm,
  head: () => ({
    meta: [
      {
        title: 'Cadastro | Clínica',
      },
    ],
  }),
})