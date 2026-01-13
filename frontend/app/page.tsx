import Link from 'next/link'

export default async function Page() {
  return (
    <>
      <div>
        <h1>Hello World</h1>
        <Link href="/we">We</Link>
      </div>
    </>
  )
}
