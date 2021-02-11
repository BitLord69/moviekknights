<template>
  <div class="about">
    <h1>JWT - JSON Web Token</h1>
    <div class="content">
    <p>
      JWT är en standard som innehåller information i form av ett JSON objekt,
      som på ett säkert sätt skickas mellan olika parter. Den innehåller
      följande 3 delar;
    </p>
    <ul>
      <li>
        Header
      </li>
      <li>
        Payload
      </li>
      <li>
        Signature
      </li>
    </ul>
    <p>
      Header består av en algoritm, som används för signaturen, och typen av
      token, i detta fallet en JWT.
    </p>
    <pre>{
  "alg": "HS256",
  "typ": "JWT"
}
    </pre>
    <p>
      Payload innehåller olika fält med information om entiteten och annan data.
      Det finns ett antal förinställda fält, men de är ej obligatoriska. Ett av
      de förinställda fälten är subject. Subject är identifierarentiteten,
      alltså till exempel hade sub-fältet kunnat vara ett användar-id. Se exempel nedan.
    </p>
    <pre>{
  "sub": "1dfee8d8-98a5-4314-b4ae-fb55c4b18845",
  "email": "ariel@codingly.io",
  "name": "Ariel Weinberger",
  "role": "ADMIN",
  "iat": 1598607423,
  "exp": 1598607723
}
    </pre>
    <p>
      Signature omkodar header och payload med hjälp av algoritmen
      från headern. Resultatet blir ett salt. Se exempel nedan.
    </p>
    <pre>
  "signature = Crypto(secret, base64(header), base64(payload))"
    </pre>
    <pre>
  "jbcOUQ2bbiYlfVtprEkaT_S6Y6yQnBDOAKDHIHjvl7g"
    </pre>
    <h3>Fördelar med JWT</h3>
    <ul>
      <li>
        Man kan använda samma JWT på olika backends.
      </li>
      <li>
        En JWT är helt stateless, vilket betyder att man ej behöver handskas med sessions.
      </li>
      <li>
        Reducerar tiden som krävs för att skicka en request och få tillbaka ett response från servern.
      </li>
      <li>
        Inga kakor behöver användas.
      </li>
    </ul>
    </div>
  </div>
</template>

<style scoped lang="scss">
@import "@/styles/_variables.scss";

.about {
  display: flex;
  flex-direction: column;
}

.content {
  text-align: left;
  width: 80%;
  margin: 0 auto;
}

pre {
  background-color: $bg-secondary;
  padding: 15px 15px 0 15px;
}

</style>
