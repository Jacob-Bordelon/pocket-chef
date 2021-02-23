const app = require("../server");
const chai = require("chai");
const chaiHttp = require("chai-http");

const { expect } = chai;
chai.use(chaiHttp);
describe("REST API tests", () => {
    it("Expect id 1 from egg", done => {
        chai
        .request(app)
        .post("/possible")
        .send([{"Ingredient": "egg"}])
        .end((err, res) => {
            expect(JSON.parse(res.text)[0].id).to.equals(1);
            done();
        });
    });
});